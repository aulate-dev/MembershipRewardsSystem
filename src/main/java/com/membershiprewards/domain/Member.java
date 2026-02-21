package com.membershiprewards.domain;

import com.membershiprewards.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.Objects;

/**
 * Member entity. Default status is ACTIVE.
 */
public class Member {

    private final String id;
    private final String fullName;
    private final String email;
    private final MembershipStatus status;
    private final Instant createdAt;

    public Member(String id, String fullName, String email, MembershipStatus status, Instant createdAt) {
        this.id = requireNonBlank(id, "id");
        this.fullName = requireNonBlank(fullName, "fullName");
        this.email = requireNonBlankAndValidEmail(email);
        this.status = status != null ? status : MembershipStatus.ACTIVE;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public Member(String id, String fullName, String email, Instant createdAt) {
        this(id, fullName, email, MembershipStatus.ACTIVE, createdAt);
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName);
        if (value.isBlank()) {
            throw new ValidationException(fieldName + " must not be blank");
        }
        return value;
    }

    private static String requireNonBlankAndValidEmail(String email) {
        String e = requireNonBlank(email, "email");
        if (!e.contains("@")) {
            throw new ValidationException("email must contain '@'");
        }
        return e;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
