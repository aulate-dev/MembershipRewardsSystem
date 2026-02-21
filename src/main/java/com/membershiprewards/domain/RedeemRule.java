package com.membershiprewards.domain;

import com.membershiprewards.domain.exceptions.ValidationException;

import java.util.Objects;

/**
 * Rule defining the cost in points and benefit for a redemption.
 */
public class RedeemRule {

    private final String id;
    private final String name;
    private final int pointsCost;
    private final String benefitDescription;
    private final boolean active;

    public RedeemRule(String id, String name, int pointsCost, String benefitDescription, boolean active) {
        this.id = requireNonBlank(id, "id");
        this.name = requireNonBlank(name, "name");
        this.pointsCost = requirePositive(pointsCost, "pointsCost");
        this.benefitDescription = requireNonBlank(benefitDescription, "benefitDescription");
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

    public int getPointsCost() {
        return pointsCost;
    }

    public String getBenefitDescription() {
        return benefitDescription;
    }

    public boolean isActive() {
        return active;
    }
}
