# Test Plan — Unit Testing

## Portada (Cover)

| Field | Value |
|-------|--------|
| **University** | [Nombre de la universidad] |
| **Course** | [Nombre del curso] |
| **Project** | MembershipRewardsSystem |
| **Deliverable** | Project 1 – Unit Testing |
| **Team member** | Ana Ulate Salas |
| **Date** | 20 Feb 2026 |

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Test Cases](#2-test-cases)
   - [MembershipService (MS-###)](#membershipservice-ms-)
   - [RulesService (RS-###)](#rulesservice-rs-)
   - [PointsService (PS-###)](#pointsservice-ps-)
3. [Results](#3-results)

---

## 1. Introduction

### Purpose of the unit tests

The unit tests verify the **business logic** of the MembershipRewardsSystem: member registration and status, earn/redeem rules, and points earning, redemption, and reporting. Tests ensure that services behave correctly for valid inputs and throw the appropriate exceptions for invalid inputs or missing data.

### Scope

- **In scope:** Business logic only — service classes that orchestrate domain entities and repositories.
- **Out of scope:** No UI; no real database or persistence layer. Repository dependencies are mocked.

### Classes under test

| Class | Responsibility |
|-------|----------------|
| **MembershipService** | Member registration, membership status (ACTIVE/INACTIVE), listing by status. |
| **RulesService** | Define and activate/deactivate earn rules and redeem rules. |
| **PointsService** | Earn points, redeem points, points history, and points summary report. |

### Tools

- **Java** (JDK 17)
- **Maven Wrapper** (`mvnw` / `mvnw.cmd`) for build and test execution
- **TestNG** as the test framework
- **Mockito** for mocking repository dependencies

---

## 2. Test Cases

Test case IDs follow the scheme: **MS-###** (MembershipServiceTest), **RS-###** (RulesServiceTest), **PS-###** (PointsServiceTest). Descriptions match the actual test method names and behavior in the codebase.

### MembershipService (MS-###)

| ID | Description | Preconditions | Steps | Expected Result | Type |
|----|-------------|---------------|--------|-----------------|------|
| MS-001 | registerMember_shouldSaveAndReturnMemberWithActive_whenValidInputs | MemberRepository is mocked; save returns the argument. | 1. Call registerMember("m1", "Jane Doe", "jane@example.com"). 2. Verify returned member and repository save. | Member returned with ACTIVE status, correct id/fullName/email; save invoked once. | Functional |
| MS-002 | registerMember_shouldThrowValidationException_whenEmailInvalid | MemberRepository is mocked. | 1. Call registerMember with email "invalid-email" (no '@'). | ValidationException (email must contain '@'); save never called. | Negative |
| MS-003 | registerMember_shouldThrowValidationException_whenIdBlank | MemberRepository is mocked. | 1. Call registerMember with id "  " (blank). | ValidationException (id must not be blank); save never called. | Negative |
| MS-004 | setMembershipStatus_shouldSaveUpdatedStatus_whenMemberExists | Member "m1" exists in repository; save returns argument. | 1. Call setMembershipStatus("m1", INACTIVE). | Returned member has INACTIVE status; findById and save each called once. | Functional |
| MS-005 | setMembershipStatus_shouldThrowMemberNotFoundException_whenMemberDoesNotExist | findById("m99") returns empty. | 1. Call setMembershipStatus("m99", INACTIVE). | MemberNotFoundException ("Member not found: m99"); save never called. | Negative |
| MS-006 | getMembershipStatus_shouldReturnStatus_whenMemberExists | Member "m1" exists with ACTIVE status. | 1. Call getMembershipStatus("m1"). | Returns ACTIVE; findById called once; save never called. | Functional |
| MS-007 | getMembershipStatus_shouldThrowMemberNotFoundException_whenNotFound | findById("m99") returns empty. | 1. Call getMembershipStatus("m99"). | MemberNotFoundException ("Member not found: m99"). | Negative |
| MS-008 | listMembersByStatus_shouldFilterCorrectly_whenMixOfActiveAndInactive | findAll returns 3 members: 2 ACTIVE, 1 INACTIVE. | 1. Call listMembersByStatus(ACTIVE). | List of 2 members, all ACTIVE; findAll called once. | Functional |
| MS-009 | listMembersByStatus_shouldThrowValidationException_whenStatusIsNull | None. | 1. Call listMembersByStatus(null). | ValidationException (status must not be null); findAll never called. | Negative |

### RulesService (RS-###)

| ID | Description | Preconditions | Steps | Expected Result | Type |
|----|-------------|---------------|--------|-----------------|------|
| RS-001 | defineEarnRule_shouldSaveAndReturn_whenValidInputs | EarnRuleRepository save returns argument. | 1. Call defineEarnRule("earn1", "Purchase", 10, true). | EarnRule returned with correct id, name, pointsPerUse, active; save called once. | Functional |
| RS-002 | defineEarnRule_shouldThrowValidationException_whenPointsPerUseZero | None. | 1. Call defineEarnRule with pointsPerUse 0. | ValidationException; save never called. | Negative |
| RS-003 | defineEarnRule_shouldThrowValidationException_whenIdBlank | None. | 1. Call defineEarnRule with ruleId "  ". | ValidationException; save never called. | Negative |
| RS-004 | setEarnRuleActive_shouldSaveUpdatedActiveFlag_whenRuleExists | findById returns existing EarnRule; save returns argument. | 1. Call setEarnRuleActive("earn1", false). | Returned rule has active=false; findById and save each called once. | Functional |
| RS-005 | setEarnRuleActive_shouldThrowRuleNotFoundException_whenNotFound | findById("earn99") returns empty. | 1. Call setEarnRuleActive("earn99", true). | RuleNotFoundException ("Earn rule not found: earn99"); save never called. | Negative |
| RS-006 | defineRedeemRule_shouldSaveAndReturn_whenValidInputs | RedeemRuleRepository save returns argument. | 1. Call defineRedeemRule("redeem1", "Voucher", 100, "10 off", true). | RedeemRule returned with correct fields; save called once. | Functional |
| RS-007 | defineRedeemRule_shouldThrowValidationException_whenPointsCostZero | None. | 1. Call defineRedeemRule with pointsCost 0. | ValidationException; save never called. | Negative |
| RS-008 | defineRedeemRule_shouldThrowValidationException_whenBenefitDescriptionBlank | None. | 1. Call defineRedeemRule with benefitDescription "  ". | ValidationException; save never called. | Negative |
| RS-009 | setRedeemRuleActive_shouldSaveUpdatedActiveFlag_whenRuleExists | findById returns existing RedeemRule; save returns argument. | 1. Call setRedeemRuleActive("redeem1", false). | Returned rule has active=false; findById and save each called once. | Functional |
| RS-010 | setRedeemRuleActive_shouldThrowRuleNotFoundException_whenNotFound | findById("redeem99") returns empty. | 1. Call setRedeemRuleActive("redeem99", true). | RuleNotFoundException ("Redeem rule not found: redeem99"); save never called. | Negative |

### PointsService (PS-###)

| ID | Description | Preconditions | Steps | Expected Result | Type |
|----|-------------|---------------|--------|-----------------|------|
| PS-001 | earnPoints_shouldUpdateAccountAndSaveTransaction_whenMemberActiveAndActiveEarnRuleAndExistingAccount | Member ACTIVE; active earn rule (15 pts); existing PointsAccount (50,50,0). Repos return/stub as needed. | 1. Call earnPoints("m1", "Purchase"). | Transaction returned (EARN, 15 pts); account saved with balance 65, totalEarned 65; transaction saved with correct type/points/memberId. | Functional |
| PS-002 | earnPoints_shouldThrowMembershipInactiveException_whenMemberInactive | Member exists with INACTIVE status. | 1. Call earnPoints("m1", "Purchase"). | MembershipInactiveException; account and transaction save never called. | Negative |
| PS-003 | earnPoints_shouldThrowRuleNotFoundException_whenNoActiveEarnRule | Member ACTIVE; findActiveRule returns empty. | 1. Call earnPoints("m1", "Purchase"). | RuleNotFoundException ("No active earn rule found"); account and transaction save never called. | Negative |
| PS-004 | redeemPoints_shouldDeductBalanceAndSaveTransaction_whenMemberActiveAndSufficientBalance | Member ACTIVE; active redeem rule (30 pts); account balance 100. | 1. Call redeemPoints("m1", "Redeem voucher"). | Transaction returned (REDEEM, 30 pts); account saved with balance 70, totalRedeemed 30; transaction saved. | Functional |
| PS-005 | redeemPoints_shouldThrowInsufficientPointsException_whenInsufficientBalance | Member ACTIVE; redeem rule cost 100; account balance 50. | 1. Call redeemPoints("m1", "Redeem"). | InsufficientPointsException; account and transaction save never called. | Negative |
| PS-006 | redeemPoints_shouldThrowRuleNotFoundException_whenNoActiveRedeemRule | Member ACTIVE; findActiveRule returns empty for redeem. | 1. Call redeemPoints("m1", "Redeem"). | RuleNotFoundException ("No active redeem rule found"); save never called. | Negative |
| PS-007 | getPointsHistory_shouldReturnListFromRepository_whenMemberExists | Member exists; findByMemberId returns a list of one transaction. | 1. Call getPointsHistory("m1"). | List of transactions returned; findById and findByMemberId each called once. | Functional |
| PS-008 | getPointsHistory_shouldThrowMemberNotFoundException_whenMemberNotFound | findById("m99") returns empty. | 1. Call getPointsHistory("m99"). | MemberNotFoundException ("Member not found: m99"); findByMemberId never called. | Negative |
| PS-009 | getPointsSummaryReport_shouldSumTotalsCorrectly_whenMembersWithAndWithoutAccounts | Two members; each has PointsAccount with known totalEarned/totalRedeemed/balance. | 1. Call getPointsSummaryReport(). | Report with summed totalEarned, totalRedeemed, totalBalance; findAll and findByMemberId invoked as expected. | Functional |
| PS-010 | getPointsSummaryReport_shouldReturnZeros_whenEmptyMemberList | findAll returns empty list. | 1. Call getPointsSummaryReport(). | Report with totalEarned=0, totalRedeemed=0, totalBalance=0; findByMemberId never called. | Boundary |

---

## 3. Results

### Test execution summary

Run the tests with Maven Wrapper:

```bash
.\mvnw test
```

**Latest run summary:**

```
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
```

**BUILD SUCCESS**

### Paste area for full output

*(Paste the full `.\mvnw test` console output below if required by the rubric.)*

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running TestSuite
...
[INFO] Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Note on warnings

Console output may show warnings from Maven (Jansi), Guava, SLF4J, or Mockito/ByteBuddy. These come from the build and test environment, not from the project code. They do **not** affect test success or the build result. All 30 tests pass.
