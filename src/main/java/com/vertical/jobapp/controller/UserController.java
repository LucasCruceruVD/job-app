package com.vertical.jobapp.controller;

import com.vertical.jobapp.dto.exceptions.*;
import com.vertical.jobapp.dto.message.*;
import com.vertical.jobapp.dto.rest.*;
import com.vertical.jobapp.model.*;
import com.vertical.jobapp.service.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final KafkaTemplate<String, UserMessageDto> kafkaTemplate;

    @Value(value = "${spring.kafka.user-topic}")
    private String USER_TOPIC;

    @GetMapping("/users/{email}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String email) {
        return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesByEmail(email).map(UserDTO::new));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        final List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        Optional<User> existingUser = userService.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        kafkaTemplate.send(USER_TOPIC, UserMessageDto.builder()
                .action(MessageAction.UPDATE)
                .user(userDTO)
                .build());

        return ResponseUtil.wrapOrNotFound(Optional.of(userDTO));
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        kafkaTemplate.send(USER_TOPIC, UserMessageDto.builder()
                .action(MessageAction.DELETE)
                .user(UserDTO.builder()
                        .email(email)
                        .build())
                .build());
        return ResponseEntity
                .noContent()
                .build();
    }

}
