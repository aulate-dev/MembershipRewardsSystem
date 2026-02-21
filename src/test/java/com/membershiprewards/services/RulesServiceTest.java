package com.membershiprewards.services;

import com.membershiprewards.domain.EarnRule;
import com.membershiprewards.domain.RedeemRule;
import com.membershiprewards.domain.exceptions.RuleNotFoundException;
import com.membershiprewards.domain.exceptions.ValidationException;
import com.membershiprewards.repositories.EarnRuleRepository;
import com.membershiprewards.repositories.RedeemRuleRepository;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for {@link RulesService}.
 */
@Test
public class RulesServiceTest {

    private EarnRuleRepository earnRuleRepository;
    private RedeemRuleRepository redeemRuleRepository;
    private RulesService rulesService;

    @BeforeMethod
    public void setUp() {
        earnRuleRepository = mock(EarnRuleRepository.class);
        redeemRuleRepository = mock(RedeemRuleRepository.class);
        rulesService = new RulesService(earnRuleRepository, redeemRuleRepository);
    }

    // --- defineEarnRule ---

    @Test
    public void defineEarnRule_shouldSaveAndReturn_whenValidInputs() {
        // Arrange
        String ruleId = "earn1";
        String name = "Purchase";
        int pointsPerUse = 10;
        boolean active = true;
        when(earnRuleRepository.save(any(EarnRule.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EarnRule result = rulesService.defineEarnRule(ruleId, name, pointsPerUse, active);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), ruleId);
        assertEquals(result.getName(), name);
        assertEquals(result.getPointsPerUse(), pointsPerUse);
        assertTrue(result.isActive());
        verify(earnRuleRepository, times(1)).save(any(EarnRule.class));
        verify(earnRuleRepository, never()).findById(any());
    }

    @Test(expectedExceptions = ValidationException.class)
    public void defineEarnRule_shouldThrowValidationException_whenPointsPerUseZero() {
        // Arrange
        String ruleId = "earn1";
        String name = "Purchase";
        int pointsPerUse = 0;
        boolean active = true;

        // Act
        rulesService.defineEarnRule(ruleId, name, pointsPerUse, active);

        // Assert (exception)
        verify(earnRuleRepository, never()).save(any(EarnRule.class));
    }

    @Test(expectedExceptions = ValidationException.class)
    public void defineEarnRule_shouldThrowValidationException_whenIdBlank() {
        // Arrange
        String ruleId = "  ";
        String name = "Purchase";
        int pointsPerUse = 10;
        boolean active = true;

        // Act
        rulesService.defineEarnRule(ruleId, name, pointsPerUse, active);

        // Assert (exception)
        verify(earnRuleRepository, never()).save(any(EarnRule.class));
    }

    // --- setEarnRuleActive ---

    @Test
    public void setEarnRuleActive_shouldSaveUpdatedActiveFlag_whenRuleExists() {
        // Arrange
        String ruleId = "earn1";
        EarnRule existing = new EarnRule(ruleId, "Purchase", 10, true);
        when(earnRuleRepository.findById(ruleId)).thenReturn(Optional.of(existing));
        when(earnRuleRepository.save(any(EarnRule.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        EarnRule result = rulesService.setEarnRuleActive(ruleId, false);

        // Assert
        assertNotNull(result);
        assertFalse(result.isActive());
        assertEquals(result.getId(), ruleId);
        verify(earnRuleRepository, times(1)).findById(ruleId);
        verify(earnRuleRepository, times(1)).save(any(EarnRule.class));
    }

    @Test(expectedExceptions = RuleNotFoundException.class, expectedExceptionsMessageRegExp = ".*Earn rule not found: earn99.*")
    public void setEarnRuleActive_shouldThrowRuleNotFoundException_whenNotFound() {
        // Arrange
        String ruleId = "earn99";
        when(earnRuleRepository.findById(ruleId)).thenReturn(Optional.empty());

        // Act
        rulesService.setEarnRuleActive(ruleId, true);

        // Assert (exception)
        verify(earnRuleRepository, never()).save(any(EarnRule.class));
    }

    // --- defineRedeemRule ---

    @Test
    public void defineRedeemRule_shouldSaveAndReturn_whenValidInputs() {
        // Arrange
        String ruleId = "redeem1";
        String name = "Voucher";
        int pointsCost = 100;
        String benefitDescription = "10 off";
        boolean active = true;
        when(redeemRuleRepository.save(any(RedeemRule.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        RedeemRule result = rulesService.defineRedeemRule(ruleId, name, pointsCost, benefitDescription, active);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), ruleId);
        assertEquals(result.getName(), name);
        assertEquals(result.getPointsCost(), pointsCost);
        assertEquals(result.getBenefitDescription(), benefitDescription);
        assertTrue(result.isActive());
        verify(redeemRuleRepository, times(1)).save(any(RedeemRule.class));
    }

    @Test(expectedExceptions = ValidationException.class)
    public void defineRedeemRule_shouldThrowValidationException_whenPointsCostZero() {
        // Arrange
        String ruleId = "redeem1";
        String name = "Voucher";
        int pointsCost = 0;
        String benefitDescription = "10 off";
        boolean active = true;

        // Act
        rulesService.defineRedeemRule(ruleId, name, pointsCost, benefitDescription, active);

        // Assert (exception)
        verify(redeemRuleRepository, never()).save(any(RedeemRule.class));
    }

    @Test(expectedExceptions = ValidationException.class)
    public void defineRedeemRule_shouldThrowValidationException_whenBenefitDescriptionBlank() {
        // Arrange
        String ruleId = "redeem1";
        String name = "Voucher";
        int pointsCost = 100;
        String benefitDescription = "  ";
        boolean active = true;

        // Act
        rulesService.defineRedeemRule(ruleId, name, pointsCost, benefitDescription, active);

        // Assert (exception)
        verify(redeemRuleRepository, never()).save(any(RedeemRule.class));
    }

    // --- setRedeemRuleActive ---

    @Test
    public void setRedeemRuleActive_shouldSaveUpdatedActiveFlag_whenRuleExists() {
        // Arrange
        String ruleId = "redeem1";
        RedeemRule existing = new RedeemRule(ruleId, "Voucher", 100, "10 off", true);
        when(redeemRuleRepository.findById(ruleId)).thenReturn(Optional.of(existing));
        when(redeemRuleRepository.save(any(RedeemRule.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        RedeemRule result = rulesService.setRedeemRuleActive(ruleId, false);

        // Assert
        assertNotNull(result);
        assertFalse(result.isActive());
        assertEquals(result.getId(), ruleId);
        verify(redeemRuleRepository, times(1)).findById(ruleId);
        verify(redeemRuleRepository, times(1)).save(any(RedeemRule.class));
    }

    @Test(expectedExceptions = RuleNotFoundException.class, expectedExceptionsMessageRegExp = ".*Redeem rule not found: redeem99.*")
    public void setRedeemRuleActive_shouldThrowRuleNotFoundException_whenNotFound() {
        // Arrange
        String ruleId = "redeem99";
        when(redeemRuleRepository.findById(ruleId)).thenReturn(Optional.empty());

        // Act
        rulesService.setRedeemRuleActive(ruleId, true);

        // Assert (exception)
        verify(redeemRuleRepository, never()).save(any(RedeemRule.class));
    }
}
