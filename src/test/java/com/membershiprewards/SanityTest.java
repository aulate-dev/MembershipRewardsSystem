package com.membershiprewards;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Minimal smoke test to verify TestNG and the build are working.
 */
public class SanityTest {

    @Test
    public void smokeTestPasses() {
        assertTrue(true, "Smoke test should pass");
    }
}
