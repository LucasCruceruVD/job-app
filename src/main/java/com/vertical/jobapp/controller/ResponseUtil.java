package com.vertical.jobapp.controller;

import org.springframework.http.*;
import org.springframework.web.server.*;

import java.util.*;

public class ResponseUtil {

    static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, (HttpHeaders)null);
    }

    static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse, HttpHeaders header) {
        return (ResponseEntity)maybeResponse.map((response) -> {
            return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().headers(header)).body(response);
        }).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });
    }

    static ProblemDetail createErrorMessage(String detail) {
        var problem =  ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail(detail);
        return problem;
    }
}
