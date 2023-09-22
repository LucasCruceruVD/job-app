package com.vertical.jobapp.repository;

import com.vertical.jobapp.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface JobRequestRepository extends JpaRepository<JobRequest, Long> {
    @Modifying
    @Query("DELETE from JobRequest jr where jr.jobListing.id = :jobListingId")
    void deleteAllByJobListingId(@Param("jobListingId") Long jobListingId);

    @Modifying
    @Query(value = "DELETE from job_request where user_id = :id", nativeQuery = true)
    void deleteAllByUserId(@Param("id")  Long id);
}
