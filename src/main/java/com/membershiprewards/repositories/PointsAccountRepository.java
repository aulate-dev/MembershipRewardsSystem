package com.membershiprewards.repositories;

import com.membershiprewards.domain.PointsAccount;

import java.util.Optional;

/**
 * Repository for persisting and querying points accounts.
 */
public interface PointsAccountRepository {

    PointsAccount save(PointsAccount account);

    Optional<PointsAccount> findByMemberId(String memberId);
}
