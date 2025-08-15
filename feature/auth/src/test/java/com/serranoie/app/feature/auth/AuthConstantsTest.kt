package com.serranoie.app.feature.auth

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthConstantsTest {

    @Test
    fun `LOGIN_EXPIRATION_TIME should equal 6 months in milliseconds`() {
        // Given
        val expectedMonths = 6L
        val expectedDaysPerMonth = 30L
        val expectedHoursPerDay = 24L
        val expectedMinutesPerHour = 60L
        val expectedSecondsPerMinute = 60L
        val expectedMillisecondsPerSecond = 1000L

        val expectedValue = expectedMonths * expectedDaysPerMonth * expectedHoursPerDay *
                expectedMinutesPerHour * expectedSecondsPerMinute * expectedMillisecondsPerSecond

        // When & Then
        assertEquals(expectedValue, AuthConstants.LOGIN_EXPIRATION_TIME)
    }

    @Test
    fun `LOGIN_EXPIRATION_TIME should equal specific milliseconds value`() {
        // Given - 6 months = 15,552,000,000 milliseconds (6 * 30 * 24 * 60 * 60 * 1000)
        val expectedMilliseconds = 15_552_000_000L

        // When & Then
        assertEquals(expectedMilliseconds, AuthConstants.LOGIN_EXPIRATION_TIME)
    }

    @Test
    fun `LOGIN_EXPIRATION_TIME should be positive value`() {
        // When & Then
        assert(AuthConstants.LOGIN_EXPIRATION_TIME > 0)
    }
}