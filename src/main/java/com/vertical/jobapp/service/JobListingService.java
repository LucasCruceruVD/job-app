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
public class JobListingService {
    private final JobListingRepository jobListingRepository;
    private final UserService userService;

    private final JobRequestRepository jobRequestRepository;

    @KafkaListener(topics = "job-listing", containerFactory = "jobListKafkaListenerContainerFactory")
    public void jobListingListener(JobListingMessageDto message) {
        log.info("Received Message in topic job-listing: " + message);
        switch (message.getAction()) {
            case CREATE -> createJobListing(message.getJobListing());
            case UPDATE -> partialUpdate(message.getJobListing());
            case DELETE -> delete(message.getJobListing().getId());
            default -> throw new MessagingException("Action " + message.getAction() +" not allowed");
        }
    }

    @Transactional(readOnly = true)
    public List<JobListing> findAll() {
        return jobListingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<JobListing> findOne(Long id) {
        return jobListingRepository.findById(id);
    }

    public void createJobListing(JobListingDTO jobListingDto) {
        JobListing jobListing = JobListing.builder()
                .id(jobListingDto.getId())
                .companyName(jobListingDto.getCompanyName())
                .title(jobListingDto.getTitle())
                .desc(jobListingDto.getDesc())
                .requirements(jobListingDto.getRequirements())
                .qualifications(jobListingDto.getQualifications())
                .minSalary(jobListingDto.getMinSalary())
                .maxSalary(jobListingDto.getMaxSalary())
                .hoursPerDay(jobListingDto.getHoursPerDay())
                .build();

        userService.findOneByEmailIgnoreCase(jobListingDto.getUserEmail()).
                ifPresentOrElse(jobListing::setUser,
                        ()->{throw new UserNotFoundException();});

        jobListingRepository.save(jobListing);
    }

    public void partialUpdate(JobListingDTO jobListingDto) {
        jobListingRepository
                .findById(jobListingDto.getId())
                .map(existingJobListing -> {
                    if (jobListingDto.getCompanyName() != null) {
                        existingJobListing.setCompanyName(jobListingDto.getCompanyName());
                    }
                    if (jobListingDto.getTitle() != null) {
                        existingJobListing.setTitle(jobListingDto.getTitle());
                    }
                    if (jobListingDto.getDesc() != null) {
                        existingJobListing.setDesc(jobListingDto.getDesc());
                    }
                    if (jobListingDto.getRequirements() != null) {
                        existingJobListing.setRequirements(jobListingDto.getRequirements());
                    }
                    if (jobListingDto.getQualifications() != null) {
                        existingJobListing.setQualifications(jobListingDto.getQualifications());
                    }
                    if (jobListingDto.getMinSalary() != null) {
                        existingJobListing.setMinSalary(jobListingDto.getMinSalary());
                    }
                    if (jobListingDto.getMaxSalary() != null) {
                        existingJobListing.setMaxSalary(jobListingDto.getMaxSalary());
                    }
                    if (jobListingDto.getHoursPerDay() != null) {
                        existingJobListing.setHoursPerDay(jobListingDto.getHoursPerDay());
                    }
                    return existingJobListing;
                })
                .map(jobListingRepository::save);
    }

    @Transactional
    public void delete(Long id) {
        jobRequestRepository.deleteAllByJobListingId(id);
        jobListingRepository.deleteById(id);
    }

}
