package com.membershiprewards.repositories;

import com.membershiprewards.domain.PointsTransaction;

import java.util.List;

/**
 * Repository for persisting and querying points transactions.
 */
public interface TransactionRepository {

    PointsTransaction save(PointsTransaction tx);

    List<PointsTransaction> findByMemberId(String memberId);
}
