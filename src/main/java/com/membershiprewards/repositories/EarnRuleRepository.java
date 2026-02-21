package com.membershiprewards.repositories;

import com.membershiprewards.domain.EarnRule;

import java.util.Optional;

/**
 * Repository for persisting and querying earn rules.
 */
public interface EarnRuleRepository {

    EarnRule save(EarnRule rule);

    Optional<EarnRule> findById(String id);

    Optional<EarnRule> findActiveRule();
}
