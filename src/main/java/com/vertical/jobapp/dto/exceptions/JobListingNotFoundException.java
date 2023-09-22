package com.vertical.jobapp.dto.exceptions;

public class JobListingNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JobListingNotFoundException() {
        super("Job Listing not found!");
    }
}
