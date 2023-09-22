package com.vertical.jobapp.repository;

import com.vertical.jobapp.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {
    @Modifying
    @Query(value = "DELETE from job_listing where user_id = :id", nativeQuery = true)
    void deleteAllByUserId(@Param("id")  Long id);
}
