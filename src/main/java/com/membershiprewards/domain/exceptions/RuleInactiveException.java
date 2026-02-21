package com.membershiprewards.domain.exceptions;

/**
 * Thrown when an operation uses an earn or redeem rule that is not active.
 */
public class RuleInactiveException extends RuntimeException {

    public RuleInactiveException(String message) {
        super(message);
    }

    public RuleInactiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
