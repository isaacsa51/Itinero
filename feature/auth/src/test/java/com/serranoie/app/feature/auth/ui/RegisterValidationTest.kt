package com.serranoie.app.feature.auth.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterValidationTest {

    @Test
    fun `validateName should return valid for proper name`() {
        // Given
        val validNames = listOf("John", "Alice", "Bob", "María", "José")

        // When & Then
        validNames.forEach { name ->
            val result = validateName(name)
            assertTrue("Name '$name' should be valid", result.isValid)
        }
    }

    @Test
    fun `validateName should return invalid for blank name`() {
        // Given
        val invalidNames = listOf("", " ", "  ")

        // When & Then
        invalidNames.forEach { name ->
            val result = validateName(name)
            assertFalse("Name '$name' should be invalid", result.isValid)
            assertTrue(result.errorMessage?.contains("required") == true)
        }
    }

    @Test
    fun `validateName should return invalid for too short name`() {
        // Given
        val shortName = "A"

        // When
        val result = validateName(shortName)

        // Then
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("at least 2 characters") == true)
    }

    @Test
    fun `validatePhoneNumber should return valid for proper phone numbers`() {
        // Given
        val validPhones = listOf("1234567890", "12345678901", "123456789012345")

        // When & Then
        validPhones.forEach { phone ->
            val result = validatePhoneNumber(phone)
            assertTrue("Phone '$phone' should be valid", result.isValid)
        }
    }

    @Test
    fun `validatePhoneNumber should return invalid for improper phone numbers`() {
        // Given
        val invalidPhones = listOf("123456789", "1234567890123456", "abc1234567", "123-456-789")

        // When & Then
        invalidPhones.forEach { phone ->
            val result = validatePhoneNumber(phone)
            assertFalse("Phone '$phone' should be invalid", result.isValid)
        }
    }

    @Test
    fun `validatePhoneNumber should return invalid for blank phone`() {
        // Given
        val blankPhone = ""

        // When
        val result = validatePhoneNumber(blankPhone)

        // Then
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("required") == true)
    }

    @Test
    fun `validateEmail should return valid for proper emails`() {
        // Given
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "test+1@gmail.com",
            "valid_email@test.org"
        )

        // When & Then
        validEmails.forEach { email ->
            val result = validateEmail(email)
            assertTrue("Email '$email' should be valid", result.isValid)
        }
    }

    @Test
    fun `validateEmail should return invalid for improper emails`() {
        // Given
        val invalidEmails = listOf(
            "test@",
            "@domain.com",
            "test.domain.com",
            "test@domain",
            "test @domain.com"
        )

        // When & Then
        invalidEmails.forEach { email ->
            val result = validateEmail(email)
            assertFalse("Email '$email' should be invalid", result.isValid)
        }
    }

    @Test
    fun `validatePassword should return valid for strong passwords`() {
        // Given
        val validPasswords = listOf(
            "Password123!",
            "StrongP@ss1",
            "MySecure#123"
        )

        // When & Then
        validPasswords.forEach { password ->
            val result = validatePassword(password)
            assertTrue("Password should be valid", result.isValid)
        }
    }

    @Test
    fun `validatePassword should return invalid for weak passwords`() {
        // Given
        val weakPasswords = listOf(
            "password", // no uppercase, number, special char
            "PASSWORD", // no lowercase, number, special char
            "Password", // no number, special char
            "Pass123",  // no special char, too short
            "Pass@",    // too short
            ""          // blank
        )

        // When & Then
        weakPasswords.forEach { password ->
            val result = validatePassword(password)
            assertFalse("Password '$password' should be invalid", result.isValid)
        }
    }

    @Test
    fun `validatePasswordConfirmation should return valid when passwords match`() {
        // Given
        val password = "Password123!"
        val confirmation = "Password123!"

        // When
        val result = validatePasswordConfirmation(password, confirmation)

        // Then
        assertTrue(result.isValid)
    }

    @Test
    fun `validatePasswordConfirmation should return invalid when passwords don't match`() {
        // Given
        val password = "Password123!"
        val confirmation = "DifferentPass123!"

        // When
        val result = validatePasswordConfirmation(password, confirmation)

        // Then
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("do not match") == true)
    }

    @Test
    fun `validatePasswordConfirmation should return invalid for blank confirmation`() {
        // Given
        val password = "Password123!"
        val confirmation = ""

        // When
        val result = validatePasswordConfirmation(password, confirmation)

        // Then
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("required") == true)
    }
}