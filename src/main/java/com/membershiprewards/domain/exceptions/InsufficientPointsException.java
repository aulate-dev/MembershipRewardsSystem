package com.membershiprewards.domain.exceptions;

/**
 * Thrown when a redemption cannot be performed due to insufficient points balance.
 */
public class InsufficientPointsException extends RuntimeException {

    public InsufficientPointsException(String message) {
        super(message);
    }

    public InsufficientPointsException(String message, Throwable cause) {
        super(message, cause);
    }
}
