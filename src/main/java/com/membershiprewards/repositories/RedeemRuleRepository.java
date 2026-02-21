package com.membershiprewards.repositories;

import com.membershiprewards.domain.RedeemRule;

import java.util.Optional;

/**
 * Repository for persisting and querying redeem rules.
 */
public interface RedeemRuleRepository {

    RedeemRule save(RedeemRule rule);

    Optional<RedeemRule> findById(String id);

    Optional<RedeemRule> findActiveRule();
}
