package com.vertical.jobapp.controller;

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

import java.net.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JobListingController {
    private final JobListingService jobListingService;
    private final KafkaTemplate<String, JobListingMessageDto> kafkaTemplate;
    @Value(value = "${spring.kafka.job-listing-topic}")
    private String jobListTopic;


    @GetMapping("/job-listings")
    public List<JobListing> getAllJobListings() {
        return jobListingService.findAll();
    }

    @GetMapping("/job-listings/{id}")
    public ResponseEntity<JobListing> getJobListing(@PathVariable Long id) {
        Optional<JobListing> jobListing = jobListingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(jobListing);
    }

    @PostMapping("/job-listings")
    public ResponseEntity<String> createJobListing(@Valid @RequestBody JobListingDTO jobListing) throws URISyntaxException {
        kafkaTemplate.send(jobListTopic, JobListingMessageDto.builder()
                        .action(MessageAction.CREATE)
                        .jobListing(jobListing)
                        .build());

        return ResponseEntity.ok()
                .body("Job listing sent for creation");
    }

    @PutMapping("/job-listings")
    public ResponseEntity<String> updateJobListing(@Valid @RequestBody JobListingDTO jobListing) {
        kafkaTemplate.send(jobListTopic, JobListingMessageDto.builder()
                .action(MessageAction.UPDATE)
                .jobListing(jobListing)
                .build());

        return ResponseEntity
            .ok()
            .body("Job listing sent for update");
    }

    @DeleteMapping("/job-listings/{id}")
    public ResponseEntity<Void> deleteJobListing(@PathVariable Long id) {
        kafkaTemplate.send(jobListTopic, JobListingMessageDto.builder()
                .action(MessageAction.DELETE)
                .jobListing(JobListingDTO
                        .builder()
                        .id(id)
                        .build())
                .build());

        return ResponseEntity
            .noContent()
            .build();
    }
}
