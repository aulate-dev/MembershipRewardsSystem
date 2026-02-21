package com.membershiprewards.domain.exceptions;

/**
 * Thrown when an operation is attempted on a member with INACTIVE status.
 */
public class MembershipInactiveException extends RuntimeException {

    public MembershipInactiveException(String message) {
        super(message);
    }

    public MembershipInactiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
