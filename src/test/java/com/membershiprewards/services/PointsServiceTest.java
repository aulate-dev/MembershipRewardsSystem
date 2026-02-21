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
import com.membershiprewards.domain.exceptions.RuleNotFoundException;
import com.membershiprewards.repositories.EarnRuleRepository;
import com.membershiprewards.repositories.MemberRepository;
import com.membershiprewards.repositories.PointsAccountRepository;
import com.membershiprewards.repositories.RedeemRuleRepository;
import com.membershiprewards.repositories.TransactionRepository;
import com.membershiprewards.services.dto.PointsSummaryReport;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for {@link PointsService}.
 */
@Test
public class PointsServiceTest {

    private MemberRepository memberRepository;
    private PointsAccountRepository pointsAccountRepository;
    private TransactionRepository transactionRepository;
    private EarnRuleRepository earnRuleRepository;
    private RedeemRuleRepository redeemRuleRepository;
    private PointsService pointsService;

    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeMethod
    public void setUp() {
        memberRepository = mock(MemberRepository.class);
        pointsAccountRepository = mock(PointsAccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        earnRuleRepository = mock(EarnRuleRepository.class);
        redeemRuleRepository = mock(RedeemRuleRepository.class);
        pointsService = new PointsService(
                memberRepository,
                pointsAccountRepository,
                transactionRepository,
                earnRuleRepository,
                redeemRuleRepository
        );
    }

    private Member createActiveMember(String id) {
        return new Member(id, "Jane Doe", "jane@example.com", MembershipStatus.ACTIVE, FIXED_INSTANT);
    }

    private Member createInactiveMember(String id) {
        return new Member(id, "Jane Doe", "jane@example.com", MembershipStatus.INACTIVE, FIXED_INSTANT);
    }

    // --- earnPoints ---

    @Test
    public void earnPoints_shouldUpdateAccountAndSaveTransaction_whenMemberActiveAndActiveEarnRuleAndExistingAccount() {
        // Arrange
        String memberId = "m1";
        String description = "Purchase";
        Member member = createActiveMember(memberId);
        EarnRule earnRule = new EarnRule("earn1", "Purchase", 15, true);
        PointsAccount existingAccount = new PointsAccount(memberId, 50, 50, 0);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(earnRuleRepository.findActiveRule()).thenReturn(Optional.of(earnRule));
        when(pointsAccountRepository.findByMemberId(memberId)).thenReturn(Optional.of(existingAccount));
        when(pointsAccountRepository.save(any(PointsAccount.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(PointsTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        PointsTransaction result = pointsService.earnPoints(memberId, description);

        // Assert
        assertNotNull(result);
        assertFalse(result.getId().isBlank());
        assertEquals(result.getMemberId(), memberId);
        assertEquals(result.getType(), TransactionType.EARN);
        assertEquals(result.getPoints(), 15);
        assertNotNull(result.getTimestamp());
        assertEquals(result.getDescription(), description);

        var accountCaptor = org.mockito.ArgumentCaptor.forClass(PointsAccount.class);
        verify(pointsAccountRepository, times(1)).save(accountCaptor.capture());
        PointsAccount savedAccount = accountCaptor.getValue();
        assertEquals(savedAccount.getMemberId(), memberId);
        assertEquals(savedAccount.getBalance(), 50 + 15);
        assertEquals(savedAccount.getTotalEarned(), 50 + 15);
        assertEquals(savedAccount.getTotalRedeemed(), 0);

        var txCaptor = org.mockito.ArgumentCaptor.forClass(PointsTransaction.class);
        verify(transactionRepository, times(1)).save(txCaptor.capture());
        PointsTransaction savedTx = txCaptor.getValue();
        assertEquals(savedTx.getType(), TransactionType.EARN);
        assertEquals(savedTx.getPoints(), 15);
        assertEquals(savedTx.getMemberId(), memberId);
    }

    @Test(expectedExceptions = MembershipInactiveException.class, expectedExceptionsMessageRegExp = ".*Membership is inactive.*")
    public void earnPoints_shouldThrowMembershipInactiveException_whenMemberInactive() {
        // Arrange
        String memberId = "m1";
        String description = "Purchase";
        Member member = createInactiveMember(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        pointsService.earnPoints(memberId, description);

        // Assert (exception)
        verify(pointsAccountRepository, never()).save(any(PointsAccount.class));
        verify(transactionRepository, never()).save(any(PointsTransaction.class));
    }

    @Test(expectedExceptions = RuleNotFoundException.class, expectedExceptionsMessageRegExp = ".*No active earn rule found.*")
    public void earnPoints_shouldThrowRuleNotFoundException_whenNoActiveEarnRule() {
        // Arrange
        String memberId = "m1";
        String description = "Purchase";
        Member member = createActiveMember(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(earnRuleRepository.findActiveRule()).thenReturn(Optional.empty());

        // Act
        pointsService.earnPoints(memberId, description);

        // Assert (exception)
        verify(pointsAccountRepository, never()).save(any(PointsAccount.class));
        verify(transactionRepository, never()).save(any(PointsTransaction.class));
    }

    // --- redeemPoints ---

    @Test
    public void redeemPoints_shouldDeductBalanceAndSaveTransaction_whenMemberActiveAndSufficientBalance() {
        // Arrange
        String memberId = "m1";
        String description = "Redeem voucher";
        Member member = createActiveMember(memberId);
        RedeemRule redeemRule = new RedeemRule("redeem1", "Voucher", 30, "10 off", true);
        PointsAccount existingAccount = new PointsAccount(memberId, 100, 100, 0);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(redeemRuleRepository.findActiveRule()).thenReturn(Optional.of(redeemRule));
        when(pointsAccountRepository.findByMemberId(memberId)).thenReturn(Optional.of(existingAccount));
        when(pointsAccountRepository.save(any(PointsAccount.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(PointsTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        PointsTransaction result = pointsService.redeemPoints(memberId, description);

        // Assert
        assertNotNull(result);
        assertFalse(result.getId().isBlank());
        assertEquals(result.getMemberId(), memberId);
        assertEquals(result.getType(), TransactionType.REDEEM);
        assertEquals(result.getPoints(), 30);
        assertNotNull(result.getTimestamp());

        var accountCaptor = org.mockito.ArgumentCaptor.forClass(PointsAccount.class);
        verify(pointsAccountRepository, times(1)).save(accountCaptor.capture());
        PointsAccount savedAccount = accountCaptor.getValue();
        assertEquals(savedAccount.getBalance(), 100 - 30);
        assertEquals(savedAccount.getTotalRedeemed(), 30);
        assertEquals(savedAccount.getTotalEarned(), 100);

        var txCaptor = org.mockito.ArgumentCaptor.forClass(PointsTransaction.class);
        verify(transactionRepository, times(1)).save(txCaptor.capture());
        assertEquals(txCaptor.getValue().getType(), TransactionType.REDEEM);
        assertEquals(txCaptor.getValue().getPoints(), 30);
    }

    @Test(expectedExceptions = InsufficientPointsException.class, expectedExceptionsMessageRegExp = ".*Insufficient points.*")
    public void redeemPoints_shouldThrowInsufficientPointsException_whenInsufficientBalance() {
        // Arrange
        String memberId = "m1";
        String description = "Redeem";
        Member member = createActiveMember(memberId);
        RedeemRule redeemRule = new RedeemRule("redeem1", "Voucher", 100, "10 off", true);
        PointsAccount existingAccount = new PointsAccount(memberId, 50, 50, 0);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(redeemRuleRepository.findActiveRule()).thenReturn(Optional.of(redeemRule));
        when(pointsAccountRepository.findByMemberId(memberId)).thenReturn(Optional.of(existingAccount));

        // Act
        pointsService.redeemPoints(memberId, description);

        // Assert (exception)
        verify(pointsAccountRepository, never()).save(any(PointsAccount.class));
        verify(transactionRepository, never()).save(any(PointsTransaction.class));
    }

    @Test(expectedExceptions = RuleNotFoundException.class, expectedExceptionsMessageRegExp = ".*No active redeem rule found.*")
    public void redeemPoints_shouldThrowRuleNotFoundException_whenNoActiveRedeemRule() {
        // Arrange
        String memberId = "m1";
        String description = "Redeem";
        Member member = createActiveMember(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(redeemRuleRepository.findActiveRule()).thenReturn(Optional.empty());

        // Act
        pointsService.redeemPoints(memberId, description);

        // Assert (exception)
        verify(pointsAccountRepository, never()).save(any(PointsAccount.class));
        verify(transactionRepository, never()).save(any(PointsTransaction.class));
    }

    // --- getPointsHistory ---

    @Test
    public void getPointsHistory_shouldReturnListFromRepository_whenMemberExists() {
        // Arrange
        String memberId = "m1";
        Member member = createActiveMember(memberId);
        PointsTransaction tx = new PointsTransaction("tx1", memberId, TransactionType.EARN, 10, FIXED_INSTANT, "desc");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(transactionRepository.findByMemberId(memberId)).thenReturn(List.of(tx));

        // Act
        List<PointsTransaction> result = pointsService.getPointsHistory(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), "tx1");
        verify(memberRepository, times(1)).findById(memberId);
        verify(transactionRepository, times(1)).findByMemberId(memberId);
    }

    @Test(expectedExceptions = MemberNotFoundException.class, expectedExceptionsMessageRegExp = ".*Member not found: m99.*")
    public void getPointsHistory_shouldThrowMemberNotFoundException_whenMemberNotFound() {
        // Arrange
        String memberId = "m99";
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act
        pointsService.getPointsHistory(memberId);

        // Assert (exception)
        verify(transactionRepository, never()).findByMemberId(any());
    }

    // --- getPointsSummaryReport ---

    @Test
    public void getPointsSummaryReport_shouldSumTotalsCorrectly_whenMembersWithAndWithoutAccounts() {
        // Arrange
        Member m1 = createActiveMember("m1");
        Member m2 = createActiveMember("m2");
        PointsAccount acc1 = new PointsAccount("m1", 20, 100, 80);
        PointsAccount acc2 = new PointsAccount("m2", 50, 50, 0); // m2 has account; m3 has no account
        when(memberRepository.findAll()).thenReturn(List.of(m1, m2));
        when(pointsAccountRepository.findByMemberId("m1")).thenReturn(Optional.of(acc1));
        when(pointsAccountRepository.findByMemberId("m2")).thenReturn(Optional.of(acc2));

        // Act
        PointsSummaryReport result = pointsService.getPointsSummaryReport();

        // Assert
        assertNotNull(result);
        assertEquals(result.getTotalEarned(), 100 + 50);
        assertEquals(result.getTotalRedeemed(), 80 + 0);
        assertEquals(result.getTotalBalance(), 20 + 50);
        verify(memberRepository, times(1)).findAll();
        verify(pointsAccountRepository, times(1)).findByMemberId("m1");
        verify(pointsAccountRepository, times(1)).findByMemberId("m2");
    }

    @Test
    public void getPointsSummaryReport_shouldReturnZeros_whenEmptyMemberList() {
        // Arrange
        when(memberRepository.findAll()).thenReturn(List.of());

        // Act
        PointsSummaryReport result = pointsService.getPointsSummaryReport();

        // Assert
        assertNotNull(result);
        assertEquals(result.getTotalEarned(), 0);
        assertEquals(result.getTotalRedeemed(), 0);
        assertEquals(result.getTotalBalance(), 0);
        verify(memberRepository, times(1)).findAll();
        verify(pointsAccountRepository, never()).findByMemberId(any());
    }
}
