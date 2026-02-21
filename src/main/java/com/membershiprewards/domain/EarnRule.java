package com.membershiprewards.domain;

import com.membershiprewards.domain.exceptions.ValidationException;

import java.util.Objects;

/**
 * Rule defining how many points are earned per use (e.g. per purchase).
 */
public class EarnRule {

    private final String id;
    private final String name;
    private final int pointsPerUse;
    private final boolean active;

    public EarnRule(String id, String name, int pointsPerUse, boolean active) {
        this.id = requireNonBlank(id, "id");
        this.name = requireNonBlank(name, "name");
        this.pointsPerUse = requirePositive(pointsPerUse, "pointsPerUse");
        this.active = active;
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

    public String getName() {
        return name;
    }

    public int getPointsPerUse() {
        return pointsPerUse;
    }

    public boolean isActive() {
        return active;
    }
}
