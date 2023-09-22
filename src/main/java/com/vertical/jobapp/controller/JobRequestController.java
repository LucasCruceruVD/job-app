package com.vertical.jobapp.controller;

import com.vertical.jobapp.dto.message.*;
import com.vertical.jobapp.dto.rest.*;
import com.vertical.jobapp.model.*;
import com.vertical.jobapp.repository.*;
import com.vertical.jobapp.service.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.*;
import org.springframework.web.*;
import org.springframework.web.bind.annotation.*;

import java.net.*;
import java.util.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JobRequestController {
    private final JobRequestService jobRequestService;
    private final KafkaTemplate<String, JobRequestMessageDto> kafkaTemplate;

    @Value(value = "${spring.kafka.job-request-topic}")
    private String jobRequirementTopic;

    @GetMapping("/job-requests")
    public List<JobRequest> getAllJobRequests() {
        return jobRequestService.findAll();
    }

    @GetMapping("/job-requests/{id}")
    public ResponseEntity<JobRequest> getJobRequest(@PathVariable Long id) {
        Optional<JobRequest> jobRequest = jobRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(jobRequest);
    }

    @PostMapping("/job-requests")
    public ResponseEntity<String> createJobRequest(@Valid @RequestBody JobRequestDTO jobRequestDTO) {
        kafkaTemplate.send(jobRequirementTopic,
                JobRequestMessageDto.builder()
                        .action(MessageAction.CREATE)
                        .jobRequest(jobRequestDTO)
                        .build());

        return ResponseEntity.ok()
            .body("Job request sent for creation");
    }

    @PutMapping(value = "/job-requests", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<String> updateJobRequest(@Valid @RequestBody JobRequestDTO jobRequestDTO) {
        kafkaTemplate.send(jobRequirementTopic, JobRequestMessageDto.builder()
                .action(MessageAction.UPDATE)
                .jobRequest(jobRequestDTO)
                .build());

        return ResponseEntity
                .ok()
                .body("Job listing sent for update");
    }

    @DeleteMapping("/job-requests/{id}")
    public ResponseEntity<Void> deleteJobRequest(@PathVariable Long id) {
        kafkaTemplate.send(jobRequirementTopic, JobRequestMessageDto.builder()
                .action(MessageAction.DELETE)
                .jobRequest(JobRequestDTO
                        .builder()
                        .id(id)
                        .build())
                .build());

        return ResponseEntity
            .noContent()
            .build();
    }
}
