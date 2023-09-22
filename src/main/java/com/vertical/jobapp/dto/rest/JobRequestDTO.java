package com.vertical.jobapp.dto.rest;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestDTO {
    private Long id;
    private byte[] cv;
    private String cvContentType;
    private String title;
    private String message;
    private Long jobListingId;
    private String userEmail;
}
