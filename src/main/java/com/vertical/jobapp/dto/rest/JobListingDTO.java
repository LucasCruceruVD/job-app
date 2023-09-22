package com.vertical.jobapp.dto.rest;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListingDTO {
    private Long id;
    private String companyName;
    private String title;
    private String desc;
    private String requirements;
    private String qualifications;
    private Long minSalary;
    private Long maxSalary;
    private Integer hoursPerDay;
    private String userEmail;
}
