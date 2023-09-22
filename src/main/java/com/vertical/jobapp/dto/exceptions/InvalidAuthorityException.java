package com.vertical.jobapp.dto.exceptions;

public class InvalidAuthorityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidAuthorityException() {
        super("Invalid Authority!");
    }
}
