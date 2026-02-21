package com.membershiprewards.domain.exceptions;

/**
 * Thrown when a member cannot be found by id.
 */
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
