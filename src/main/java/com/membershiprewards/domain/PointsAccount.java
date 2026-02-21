package com.membershiprewards.domain;

import com.membershiprewards.domain.exceptions.ValidationException;

import java.util.Objects;

/**
 * Points account for a member. All point amounts must be non-negative.
 */
public class PointsAccount {

    private final String memberId;
    private final int balance;
    private final int totalEarned;
    private final int totalRedeemed;

    public PointsAccount(String memberId, int balance, int totalEarned, int totalRedeemed) {
        this.memberId = requireNonBlank(memberId, "memberId");
        this.balance = requireNonNegative(balance, "balance");
        this.totalEarned = requireNonNegative(totalEarned, "totalEarned");
        this.totalRedeemed = requireNonNegative(totalRedeemed, "totalRedeemed");
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName);
        if (value.isBlank()) {
            throw new ValidationException(fieldName + " must not be blank");
        }
        return value;
    }

    private static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName + " must be non-negative");
        }
        return value;
    }

    public String getMemberId() {
        return memberId;
    }

    public int getBalance() {
        return balance;
    }

    public int getTotalEarned() {
        return totalEarned;
    }

    public int getTotalRedeemed() {
        return totalRedeemed;
    }
}
