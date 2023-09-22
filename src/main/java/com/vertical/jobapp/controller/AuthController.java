package com.vertical.jobapp.controller;

import com.vertical.jobapp.config.security.*;
import com.vertical.jobapp.dto.exceptions.*;
import com.vertical.jobapp.dto.rest.*;
import com.vertical.jobapp.model.User;
import com.vertical.jobapp.service.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO login) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));

        UserDetails user = userService.loadUserByUsername(login.getEmail());
        if (user != null) {
            return ResponseEntity.ok(jwtUtil.generateToken(user));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new RuntimeException("A new user cannot already have an ID");
        } else if (userService.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
            return ResponseEntity.ok()
                    .body(newUser);
        }
    }
}

