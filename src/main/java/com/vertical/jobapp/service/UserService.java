package com.vertical.jobapp.service;

import com.vertical.jobapp.dto.*;
import com.vertical.jobapp.dto.exceptions.*;
import com.vertical.jobapp.dto.message.*;
import com.vertical.jobapp.dto.rest.*;
import com.vertical.jobapp.model.User;
import com.vertical.jobapp.model.*;
import com.vertical.jobapp.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.kafka.annotation.*;
import org.springframework.messaging.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobListingRepository jobListingRepository;
    private final JobRequestRepository jobRequestRepository;

    @KafkaListener(topics = "user", containerFactory = "userKafkaListenerContainerFactory")
    public void userListener(UserMessageDto message) {
        log.info("Received Message in topic users: " + message);
        switch (message.getAction()) {
            case CREATE -> createUser(message.getUser());
            case UPDATE -> updateUser(message.getUser());
            case DELETE -> deleteUser(message.getUser().getEmail());
            default -> throw new MessagingException("Action " + message.getAction() +" not allowed");
        }
    }

    public User createUser(UserDTO userDTO) {
        userRepository
                .findOneByEmailIgnoreCase(userDTO.getEmail())
                .ifPresent(existingUser -> {
                    throw new EmailAlreadyUsedException();
                });
        User newUser = new User();
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail());
        }
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getPhoneNr() != null) {
            newUser.setPhoneNr(userDTO.getPhoneNr().toLowerCase());
        }
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.ROLE_USER.name()).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        return newUser;
    }


    public void deleteUser (String email){
        deleteAllDependenciesByEmail(email);
        
        userRepository
                .findOneByEmail(email)
                .ifPresent(userRepository::delete);
    }

    public void updateUser(UserDTO userDTO) {
        Optional
                .of(userRepository.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    if (userDTO.getPhoneNr() != null) {
                        user.setPhoneNr(userDTO.getPhoneNr().toLowerCase());
                    }
                    if (userDTO.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    }
                    if (userDTO.getFirstName() != null) {
                        user.setFirstName(userDTO.getFirstName());
                    }
                    if (userDTO.getLastName() != null) {
                        user.setLastName(userDTO.getLastName());
                    }

                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO
                            .getAuthorities()
                            .stream()
                            .map(authorityRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .forEach(managedAuthorities::add);
                    userRepository.save(user);
                    return user;
                })
                .map(UserDTO::new);
    }

    @Override
    public UserDetails loadUserByUsername (String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findOneByEmail(email);
        if (userOpt.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userOpt.get().getEmail())
                    .password(userOpt.get().getPassword())
                    .authorities(userOpt.get().getAuthorities()
                            .stream()
                            .map(Authority::getName)
                            .map(SimpleGrantedAuthority::new)
                            .toList())
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByEmail(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public Optional<User> findOneByEmailIgnoreCase(String email) {
        return userRepository.findOneByEmailIgnoreCase(email);
    }

    @Transactional
    public void deleteAllDependenciesByEmail(String email) {
        Optional<User> userOptional = userRepository.findOneByEmailIgnoreCase(email);
        userOptional.ifPresent( user -> {
            jobRequestRepository.deleteAllByUserId(user.getId());
            jobListingRepository.deleteAllByUserId(user.getId());
        } );
    }
    
}
