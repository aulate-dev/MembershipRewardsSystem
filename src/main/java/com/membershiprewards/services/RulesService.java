package com.membershiprewards.services;

import com.membershiprewards.domain.EarnRule;
import com.membershiprewards.domain.RedeemRule;
import com.membershiprewards.domain.exceptions.RuleNotFoundException;
import com.membershiprewards.repositories.EarnRuleRepository;
import com.membershiprewards.repositories.RedeemRuleRepository;

import java.util.Objects;

/**
 * Service for defining and managing earn and redeem rules.
 */
public class RulesService {

    private final EarnRuleRepository earnRuleRepository;
    private final RedeemRuleRepository redeemRuleRepository;

    public RulesService(EarnRuleRepository earnRuleRepository, RedeemRuleRepository redeemRuleRepository) {
        this.earnRuleRepository = Objects.requireNonNull(earnRuleRepository, "earnRuleRepository");
        this.redeemRuleRepository = Objects.requireNonNull(redeemRuleRepository, "redeemRuleRepository");
    }

    /**
     * Define a new earn rule.
     */
    public EarnRule defineEarnRule(String ruleId, String name, int pointsPerUse, boolean active) {
        EarnRule rule = new EarnRule(ruleId, name, pointsPerUse, active);
        return earnRuleRepository.save(rule);
    }

    /**
     * Set whether the earn rule is active.
     */
    public EarnRule setEarnRuleActive(String ruleId, boolean active) {
        EarnRule existing = earnRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuleNotFoundException("Earn rule not found: " + ruleId));
        EarnRule updated = new EarnRule(
                existing.getId(),
                existing.getName(),
                existing.getPointsPerUse(),
                active
        );
        return earnRuleRepository.save(updated);
    }

    /**
     * Define a new redeem rule.
     */
    public RedeemRule defineRedeemRule(String ruleId, String name, int pointsCost,
                                        String benefitDescription, boolean active) {
        RedeemRule rule = new RedeemRule(ruleId, name, pointsCost, benefitDescription, active);
        return redeemRuleRepository.save(rule);
    }

    /**
     * Set whether the redeem rule is active.
     */
    public RedeemRule setRedeemRuleActive(String ruleId, boolean active) {
        RedeemRule existing = redeemRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuleNotFoundException("Redeem rule not found: " + ruleId));
        RedeemRule updated = new RedeemRule(
                existing.getId(),
                existing.getName(),
                existing.getPointsCost(),
                existing.getBenefitDescription(),
                active
        );
        return redeemRuleRepository.save(updated);
    }
}
