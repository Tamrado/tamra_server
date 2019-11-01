package com.webapp.timeline.sns.service.exception;

public class UnauthorizedUserException extends RuntimeException {

    public UnauthorizedUserException() {
        super("UNAUTHORIZED-USER EXCEPTION");
    }

    public UnauthorizedUserException(String message) {
        super(message);
    }
}
