package com.membershiprewards.services;

import com.membershiprewards.domain.Member;
import com.membershiprewards.domain.MembershipStatus;
import com.membershiprewards.domain.exceptions.MemberNotFoundException;
import com.membershiprewards.domain.exceptions.ValidationException;
import com.membershiprewards.repositories.MemberRepository;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for {@link MembershipService}.
 */
@Test
public class MembershipServiceTest {

    private MemberRepository memberRepository;
    private MembershipService membershipService;

    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeMethod
    public void setUp() {
        memberRepository = mock(MemberRepository.class);
        membershipService = new MembershipService(memberRepository);
    }

    private Member createMember(String id, String fullName, String email, MembershipStatus status) {
        return new Member(id, fullName, email, status, FIXED_INSTANT);
    }

    // --- registerMember ---

    @Test
    public void registerMember_shouldSaveAndReturnMemberWithActive_whenValidInputs() {
        // Arrange
        String id = "m1";
        String fullName = "Jane Doe";
        String email = "jane@example.com";
        Member savedMember = createMember(id, fullName, email, MembershipStatus.ACTIVE);
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Member result = membershipService.registerMember(id, fullName, email);

        // Assert
        assertNotNull(result);
        assertEquals(result.getStatus(), MembershipStatus.ACTIVE);
        assertEquals(result.getId(), id);
        assertEquals(result.getFullName(), fullName);
        assertEquals(result.getEmail(), email);
        assertNotNull(result.getCreatedAt());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test(expectedExceptions = ValidationException.class, expectedExceptionsMessageRegExp = ".*email must contain '@'.*")
    public void registerMember_shouldThrowValidationException_whenEmailInvalid() {
        // Arrange
        String id = "m1";
        String fullName = "Jane Doe";
        String email = "invalid-email";

        // Act
        membershipService.registerMember(id, fullName, email);

        // Assert (exception)
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test(expectedExceptions = ValidationException.class, expectedExceptionsMessageRegExp = ".*id must not be blank.*")
    public void registerMember_shouldThrowValidationException_whenIdBlank() {
        // Arrange
        String id = "  ";
        String fullName = "Jane Doe";
        String email = "jane@example.com";

        // Act
        membershipService.registerMember(id, fullName, email);

        // Assert (exception)
        verify(memberRepository, never()).save(any(Member.class));
    }

    // --- setMembershipStatus ---

    @Test
    public void setMembershipStatus_shouldSaveUpdatedStatus_whenMemberExists() {
        // Arrange
        String memberId = "m1";
        Member existing = createMember(memberId, "Jane", "jane@ex.com", MembershipStatus.ACTIVE);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existing));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Member result = membershipService.setMembershipStatus(memberId, MembershipStatus.INACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(result.getStatus(), MembershipStatus.INACTIVE);
        assertEquals(result.getId(), memberId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test(expectedExceptions = MemberNotFoundException.class, expectedExceptionsMessageRegExp = ".*Member not found: m99.*")
    public void setMembershipStatus_shouldThrowMemberNotFoundException_whenMemberDoesNotExist() {
        // Arrange
        String memberId = "m99";
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act
        membershipService.setMembershipStatus(memberId, MembershipStatus.INACTIVE);

        // Assert (exception)
        verify(memberRepository, never()).save(any(Member.class));
    }

    // --- getMembershipStatus ---

    @Test
    public void getMembershipStatus_shouldReturnStatus_whenMemberExists() {
        // Arrange
        String memberId = "m1";
        Member member = createMember(memberId, "Jane", "jane@ex.com", MembershipStatus.ACTIVE);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        MembershipStatus status = membershipService.getMembershipStatus(memberId);

        // Assert
        assertEquals(status, MembershipStatus.ACTIVE);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test(expectedExceptions = MemberNotFoundException.class, expectedExceptionsMessageRegExp = ".*Member not found: m99.*")
    public void getMembershipStatus_shouldThrowMemberNotFoundException_whenNotFound() {
        // Arrange
        String memberId = "m99";
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act
        membershipService.getMembershipStatus(memberId);

        // Assert (exception)
    }

    // --- listMembersByStatus ---

    @Test
    public void listMembersByStatus_shouldFilterCorrectly_whenMixOfActiveAndInactive() {
        // Arrange
        Member active1 = createMember("m1", "A", "a@x.com", MembershipStatus.ACTIVE);
        Member inactive1 = createMember("m2", "B", "b@x.com", MembershipStatus.INACTIVE);
        Member active2 = createMember("m3", "C", "c@x.com", MembershipStatus.ACTIVE);
        when(memberRepository.findAll()).thenReturn(List.of(active1, inactive1, active2));

        // Act
        List<Member> result = membershipService.listMembersByStatus(MembershipStatus.ACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertTrue(result.stream().allMatch(m -> m.getStatus() == MembershipStatus.ACTIVE));
        verify(memberRepository, times(1)).findAll();
    }

    @Test(expectedExceptions = ValidationException.class, expectedExceptionsMessageRegExp = ".*status must not be null.*")
    public void listMembersByStatus_shouldThrowValidationException_whenStatusIsNull() {
        // Act
        membershipService.listMembersByStatus(null);

        // Assert (exception)
        verify(memberRepository, never()).findAll();
    }
}
