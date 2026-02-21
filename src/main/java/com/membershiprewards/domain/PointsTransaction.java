package com.membershiprewards.domain;

import com.membershiprewards.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.Objects;

/**
 * A single points earn or redeem transaction.
 */
public class PointsTransaction {

    private final String id;
    private final String memberId;
    private final TransactionType type;
    private final int points;
    private final Instant timestamp;
    private final String description;

    public PointsTransaction(String id, String memberId, TransactionType type, int points,
                             Instant timestamp, String description) {
        this.id = requireNonBlank(id, "id");
        this.memberId = requireNonBlank(memberId, "memberId");
        this.type = Objects.requireNonNull(type, "type");
        this.points = requirePositive(points, "points");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.description = description != null ? description : "";
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName);
        if (value.isBlank()) {
            throw new ValidationException(fieldName + " must not be blank");
        }
        return value;
    }

    private static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than 0");
        }
        return value;
    }

    public String getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public TransactionType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
}
