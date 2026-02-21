package com.membershiprewards.domain.exceptions;

/**
 * Thrown when an earn or redeem rule cannot be found by id.
 */
public class RuleNotFoundException extends RuntimeException {

    public RuleNotFoundException(String message) {
        super(message);
    }

    public RuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
