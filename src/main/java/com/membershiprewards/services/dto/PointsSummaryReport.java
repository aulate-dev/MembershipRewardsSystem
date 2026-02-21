package com.membershiprewards.services.dto;

/**
 * Summary of points totals across all members.
 */
public class PointsSummaryReport {

    private final int totalEarned;
    private final int totalRedeemed;
    private final int totalBalance;

    public PointsSummaryReport(int totalEarned, int totalRedeemed, int totalBalance) {
        this.totalEarned = totalEarned;
        this.totalRedeemed = totalRedeemed;
        this.totalBalance = totalBalance;
    }

    public int getTotalEarned() {
        return totalEarned;
    }

    public int getTotalRedeemed() {
        return totalRedeemed;
    }

    public int getTotalBalance() {
        return totalBalance;
    }
}
