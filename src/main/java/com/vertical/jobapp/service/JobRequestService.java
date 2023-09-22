package com.vertical.jobapp.service;

import com.vertical.jobapp.dto.exceptions.*;
import com.vertical.jobapp.dto.message.*;
import com.vertical.jobapp.dto.rest.*;
import com.vertical.jobapp.model.*;
import com.vertical.jobapp.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.kafka.annotation.*;
import org.springframework.messaging.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobRequestService {
    private final JobRequestRepository jobRequestRepository;
    private final UserService userService;
    private final JobListingService jobListingService;

    @KafkaListener(topics = "job-request", containerFactory = "jobRequestKafkaListenerContainerFactory")
    public void jobRequestListener(JobRequestMessageDto message) {
        log.info("Received Message in topic job-request: " + message);
        switch (message.getAction()) {
            case CREATE -> createJobRequest(message.getJobRequest());
            case UPDATE -> partialUpdate(message.getJobRequest());
            case DELETE -> delete(message.getJobRequest().getId());
            default -> throw new MessagingException("Action " + message.getAction() +" not allowed");
        }
    }

    @Transactional(readOnly = true)
    public List<JobRequest> findAll() {
        return jobRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<JobRequest> findOne(Long id) {
        return jobRequestRepository.findById(id);
    }

    public void createJobRequest(JobRequestDTO jobRequestDTO) {
        JobRequest jobRequest = JobRequest.builder()
                .id(jobRequestDTO.getId())
                .cvContentType(jobRequestDTO.getCvContentType())
                .cv(jobRequestDTO.getCv())
                .title(jobRequestDTO.getTitle())
                .message(jobRequestDTO.getMessage())
                .build();
        userService.findOneByEmailIgnoreCase(jobRequestDTO.getUserEmail())
                .ifPresentOrElse(jobRequest::setUser,
                        ()->{throw new UserNotFoundException();});

        jobListingService.findOne(jobRequestDTO.getJobListingId())
                .ifPresentOrElse(jobRequest::setJobListing,
                        ()->{throw new JobListingNotFoundException();});

        jobRequestRepository.save(jobRequest);
    }

    public void partialUpdate(JobRequestDTO jobRequestDTO) {
        jobRequestRepository
                .findById(jobRequestDTO.getId())
                .map(existingJobRequest -> {
                    if (jobRequestDTO.getCv() != null) {
                        existingJobRequest.setCv(jobRequestDTO.getCv());
                    }
                    if (jobRequestDTO.getCvContentType() != null) {
                        existingJobRequest.setCvContentType(jobRequestDTO.getCvContentType());
                    }
                    if (jobRequestDTO.getTitle() != null) {
                        existingJobRequest.setTitle(jobRequestDTO.getTitle());
                    }
                    if (jobRequestDTO.getMessage() != null) {
                        existingJobRequest.setMessage(jobRequestDTO.getMessage());
                    }
                    return existingJobRequest;
                })
                .map(jobRequestRepository::save);
    }

    public void delete(Long id) {
        jobRequestRepository.deleteById(id);
    }
}
