package com.membershiprewards.services;

import com.membershiprewards.domain.EarnRule;
import com.membershiprewards.domain.Member;
import com.membershiprewards.domain.MembershipStatus;
import com.membershiprewards.domain.PointsAccount;
import com.membershiprewards.domain.PointsTransaction;
import com.membershiprewards.domain.RedeemRule;
import com.membershiprewards.domain.TransactionType;
import com.membershiprewards.domain.exceptions.InsufficientPointsException;
import com.membershiprewards.domain.exceptions.MemberNotFoundException;
import com.membershiprewards.domain.exceptions.MembershipInactiveException;
import com.membershiprewards.domain.exceptions.RuleInactiveException;
import com.membershiprewards.domain.exceptions.RuleNotFoundException;
import com.membershiprewards.domain.exceptions.ValidationException;
import com.membershiprewards.repositories.EarnRuleRepository;
import com.membershiprewards.repositories.MemberRepository;
import com.membershiprewards.repositories.PointsAccountRepository;
import com.membershiprewards.repositories.RedeemRuleRepository;
import com.membershiprewards.repositories.TransactionRepository;
import com.membershiprewards.services.dto.PointsSummaryReport;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for earning, redeeming, and reporting points.
 */
public class PointsService {

    private final MemberRepository memberRepository;
    private final PointsAccountRepository pointsAccountRepository;
    private final TransactionRepository transactionRepository;
    private final EarnRuleRepository earnRuleRepository;
    private final RedeemRuleRepository redeemRuleRepository;

    public PointsService(MemberRepository memberRepository,
                         PointsAccountRepository pointsAccountRepository,
                         TransactionRepository transactionRepository,
                         EarnRuleRepository earnRuleRepository,
                         RedeemRuleRepository redeemRuleRepository) {
        this.memberRepository = Objects.requireNonNull(memberRepository, "memberRepository");
        this.pointsAccountRepository = Objects.requireNonNull(pointsAccountRepository, "pointsAccountRepository");
        this.transactionRepository = Objects.requireNonNull(transactionRepository, "transactionRepository");
        this.earnRuleRepository = Objects.requireNonNull(earnRuleRepository, "earnRuleRepository");
        this.redeemRuleRepository = Objects.requireNonNull(redeemRuleRepository, "redeemRuleRepository");
    }

    /**
     * Earn points for a member using the active earn rule.
     */
    public PointsTransaction earnPoints(String memberId, String description) {
        validateNonBlank(memberId, "memberId");
        validateNonBlank(description, "description");
        Member member = findActiveMemberOrThrow(memberId);
        EarnRule rule = findActiveEarnRuleOrThrow();
        if (!rule.isActive()) {
            throw new RuleInactiveException("Earn rule is not active: " + rule.getId());
        }
        PointsAccount account = pointsAccountRepository.findByMemberId(memberId)
                .orElse(new PointsAccount(memberId, 0, 0, 0));
        int newBalance = account.getBalance() + rule.getPointsPerUse();
        int newTotalEarned = account.getTotalEarned() + rule.getPointsPerUse();
        PointsAccount updated = new PointsAccount(member.getId(), newBalance, newTotalEarned, account.getTotalRedeemed());
        pointsAccountRepository.save(updated);
        Instant now = Instant.now();
        PointsTransaction tx = new PointsTransaction(
                UUID.randomUUID().toString(),
                memberId,
                TransactionType.EARN,
                rule.getPointsPerUse(),
                now,
                description
        );
        return transactionRepository.save(tx);
    }

    /**
     * Redeem points for a member using the active redeem rule.
     */
    public PointsTransaction redeemPoints(String memberId, String description) {
        validateNonBlank(memberId, "memberId");
        validateNonBlank(description, "description");
        Member member = findActiveMemberOrThrow(memberId);
        RedeemRule rule = findActiveRedeemRuleOrThrow();
        if (!rule.isActive()) {
            throw new RuleInactiveException("Redeem rule is not active: " + rule.getId());
        }
        int balance = pointsAccountRepository.findByMemberId(memberId)
                .map(PointsAccount::getBalance)
                .orElse(0);
        if (balance < rule.getPointsCost()) {
            throw new InsufficientPointsException(
                    "Insufficient points: balance=" + balance + ", required=" + rule.getPointsCost());
        }
        PointsAccount account = pointsAccountRepository.findByMemberId(memberId)
                .orElse(new PointsAccount(memberId, 0, 0, 0));
        int newBalance = account.getBalance() - rule.getPointsCost();
        int newTotalRedeemed = account.getTotalRedeemed() + rule.getPointsCost();
        PointsAccount updated = new PointsAccount(
                memberId,
                newBalance,
                account.getTotalEarned(),
                newTotalRedeemed
        );
        pointsAccountRepository.save(updated);
        Instant now = Instant.now();
        PointsTransaction tx = new PointsTransaction(
                UUID.randomUUID().toString(),
                memberId,
                TransactionType.REDEEM,
                rule.getPointsCost(),
                now,
                description
        );
        return transactionRepository.save(tx);
    }

    /**
     * Get points transaction history for a member.
     */
    public List<PointsTransaction> getPointsHistory(String memberId) {
        validateNonBlank(memberId, "memberId");
        findMemberOrThrow(memberId);
        return transactionRepository.findByMemberId(memberId);
    }

    /**
     * Produce a summary of total earned, redeemed, and balance across all members.
     */
    public PointsSummaryReport getPointsSummaryReport() {
        List<Member> members = memberRepository.findAll();
        int totalEarned = 0;
        int totalRedeemed = 0;
        int totalBalance = 0;
        for (Member member : members) {
            Optional<PointsAccount> accountOpt = pointsAccountRepository.findByMemberId(member.getId());
            if (accountOpt.isPresent()) {
                PointsAccount acc = accountOpt.get();
                totalEarned += acc.getTotalEarned();
                totalRedeemed += acc.getTotalRedeemed();
                totalBalance += acc.getBalance();
            }
        }
        return new PointsSummaryReport(totalEarned, totalRedeemed, totalBalance);
    }

    private Member findMemberOrThrow(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found: " + memberId));
    }

    private Member findActiveMemberOrThrow(String memberId) {
        Member member = findMemberOrThrow(memberId);
        if (member.getStatus() != MembershipStatus.ACTIVE) {
            throw new MembershipInactiveException("Membership is inactive for member: " + memberId);
        }
        return member;
    }

    private EarnRule findActiveEarnRuleOrThrow() {
        return earnRuleRepository.findActiveRule()
                .orElseThrow(() -> new RuleNotFoundException("No active earn rule found"));
    }

    private RedeemRule findActiveRedeemRuleOrThrow() {
        return redeemRuleRepository.findActiveRule()
                .orElseThrow(() -> new RuleNotFoundException("No active redeem rule found"));
    }

    private static void validateNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " must not be blank");
        }
    }
}
