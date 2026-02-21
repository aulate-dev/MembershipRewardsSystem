package com.membershiprewards.services;

import com.membershiprewards.domain.Member;
import com.membershiprewards.domain.MembershipStatus;
import com.membershiprewards.domain.exceptions.MemberNotFoundException;
import com.membershiprewards.domain.exceptions.ValidationException;
import com.membershiprewards.repositories.MemberRepository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Service for member registration and membership status.
 */
public class MembershipService {

    private final MemberRepository memberRepository;

    public MembershipService(MemberRepository memberRepository) {
        this.memberRepository = Objects.requireNonNull(memberRepository, "memberRepository");
    }

    /**
     * Register a new member with ACTIVE status.
     */
    public Member registerMember(String id, String fullName, String email) {
        validateNonBlank(id, "id");
        validateNonBlank(fullName, "fullName");
        validateEmail(email);
        Instant now = Instant.now();
        Member member = new Member(id, fullName, email, MembershipStatus.ACTIVE, now);
        return memberRepository.save(member);
    }

    /**
     * Update a member's membership status.
     */
    public Member setMembershipStatus(String memberId, MembershipStatus status) {
        validateNonBlank(memberId, "memberId");
        Objects.requireNonNull(status, "status");
        Member existing = findMemberOrThrow(memberId);
        Member updated = new Member(
                existing.getId(),
                existing.getFullName(),
                existing.getEmail(),
                status,
                existing.getCreatedAt()
        );
        return memberRepository.save(updated);
    }

    /**
     * Get the current membership status for a member.
     */
    public MembershipStatus getMembershipStatus(String memberId) {
        validateNonBlank(memberId, "memberId");
        Member member = findMemberOrThrow(memberId);
        return member.getStatus();
    }

    /**
     * List all members with the given status.
     */
    public List<Member> listMembersByStatus(MembershipStatus status) {
        if (status == null) {
            throw new ValidationException("status must not be null");
        }
        return memberRepository.findAll().stream()
                .filter(m -> status.equals(m.getStatus()))
                .toList();
    }

    private Member findMemberOrThrow(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found: " + memberId));
    }

    private static void validateNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " must not be blank");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("email must not be blank");
        }
        if (!email.contains("@")) {
            throw new ValidationException("email must contain '@'");
        }
    }
}
