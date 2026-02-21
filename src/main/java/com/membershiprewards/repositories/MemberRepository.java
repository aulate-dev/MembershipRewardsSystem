package com.membershiprewards.repositories;

import com.membershiprewards.domain.Member;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting and querying members.
 */
public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(String id);

    List<Member> findAll();
}
