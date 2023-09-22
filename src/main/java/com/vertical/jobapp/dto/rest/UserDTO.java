package com.vertical.jobapp.dto.rest;

import com.vertical.jobapp.model.*;
import lombok.*;

import java.util.*;
import java.util.stream.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNr;
    private Set<String> authorities = new HashSet<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = "REDACTED";
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNr = user.getPhoneNr();
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }
}
