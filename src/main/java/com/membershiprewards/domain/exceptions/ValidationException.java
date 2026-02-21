package com.membershiprewards.domain.exceptions;

/**
 * Thrown when domain validation fails (e.g. blank id, invalid email, negative points).
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
