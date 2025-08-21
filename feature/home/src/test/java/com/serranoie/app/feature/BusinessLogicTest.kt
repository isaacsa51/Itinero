package com.serranoie.app.feature

import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.CreateTrip
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull

class BusinessLogicTest {

    @Test
    fun `CreateTrip validation logic works correctly`() {
        fun isValidCreateTrip(createTrip: CreateTrip): Boolean {
            return createTrip.groupName.isNotBlank() &&
                    createTrip.destination.isNotBlank() &&
                    createTrip.startDate.isNotBlank() &&
                    createTrip.endDate.isNotBlank() &&
                    createTrip.summary.isNotBlank() &&
                    createTrip.accommodation.name.isNotBlank() &&
                    createTrip.accommodation.phone.isNotBlank() &&
                    createTrip.accommodation.location.isNotBlank() &&
                    createTrip.reservationCode.isNotBlank() &&
                    createTrip.extraInfo.isNotBlank() &&
                    createTrip.additionalInfo.isNotBlank()
        }

        val validAccommodation = Accommodation(
            name = "Test Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo",
            mapUri = null
        )

        val validTrip = CreateTrip(
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            accommodation = validAccommodation,
            reservationCode = "RES123",
            extraInfo = "Extra info",
            additionalInfo = "Additional info"
        )

        assertTrue("Valid trip should pass validation", isValidCreateTrip(validTrip))

        assertFalse(
            "Empty group name should fail",
            isValidCreateTrip(validTrip.copy(groupName = ""))
        )
        assertFalse(
            "Empty destination should fail",
            isValidCreateTrip(validTrip.copy(destination = ""))
        )
        assertFalse(
            "Empty start date should fail",
            isValidCreateTrip(validTrip.copy(startDate = ""))
        )
        assertFalse("Empty end date should fail", isValidCreateTrip(validTrip.copy(endDate = "")))
        assertFalse("Empty summary should fail", isValidCreateTrip(validTrip.copy(summary = "")))

        val invalidAccommodation = validAccommodation.copy(name = "")
        assertFalse(
            "Empty accommodation name should fail",
            isValidCreateTrip(validTrip.copy(accommodation = invalidAccommodation))
        )
    }

    @Test
    fun `accommodation mapUri business rule works correctly`() {
        fun processMapUri(mapUri: String): String? {
            return mapUri.takeIf { it.isNotBlank() }
        }

        assertEquals(
            "Valid URI should be preserved", "https://maps.google.com",
            processMapUri("https://maps.google.com")
        )
        assertNull("Empty string should become null", processMapUri(""))
        assertNull("Whitespace only should become null", processMapUri("   "))
        assertEquals(
            "Valid URI with content should be preserved", "https://example.com",
            processMapUri("https://example.com")
        )
    }

    @Test
    fun `group code formatting logic works correctly`() {
        fun formatGroupCode(code: String): String {
            return if (code.startsWith("ITN-")) code else "ITN-$code"
        }

        assertEquals("ITN-12345", formatGroupCode("12345"))
        assertEquals("ITN-ABCDE", formatGroupCode("ABCDE"))
        assertEquals("ITN-12345", formatGroupCode("ITN-12345")) // Already formatted
    }

    @Test
    fun `trip creation parameters validation`() {
        fun validateTripCreationParameters(
            groupName: String,
            destination: String,
            startDate: String,
            endDate: String,
            summary: String,
            accommodationName: String,
            accommodationPhone: String,
            accommodationCheckIn: String,
            accommodationCheckOut: String,
            accommodationLocation: String,
            accommodationMapUri: String,
            reservationCode: String,
            extraInfo: String,
            additionalInfo: String
        ): Boolean {
            // Basic validation
            val hasRequiredFields = listOf(
                groupName, destination, startDate, endDate, summary,
                accommodationName, accommodationPhone, accommodationCheckIn, accommodationCheckOut,
                accommodationLocation, reservationCode, extraInfo, additionalInfo
            ).all { it.isNotBlank() }

            val processedMapUri = accommodationMapUri.takeIf { it.isNotBlank() }

            return hasRequiredFields
        }

        assertTrue(
            "All required fields should pass", validateTripCreationParameters(
                "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
                "Hotel", "123456789", "2025-06-15", "2025-06-20", "Location",
                "https://maps.google.com", "RES123", "Extra", "Additional"
            )
        )

        assertFalse(
            "Missing field should fail", validateTripCreationParameters(
                "", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
                "Hotel", "123456789", "2025-06-15", "2025-06-20", "Location",
                "https://maps.google.com", "RES123", "Extra", "Additional"
            )
        )

        assertTrue(
            "Empty map URI should still pass", validateTripCreationParameters(
                "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
                "Hotel", "123456789", "2025-06-15", "2025-06-20", "Location",
                "", "RES123", "Extra", "Additional"
            )
        )
    }

    @Test
    fun `page navigation validation logic`() {
        fun canAdvanceFromBasicPage(
            groupName: String,
            destination: String,
            startDate: String,
            endDate: String,
            summary: String
        ): Boolean {
            return groupName.isNotBlank() && destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && summary.isNotBlank()
        }

        fun canAdvanceFromAccommodationPage(
            name: String,
            phone: String,
            checkInDate: Long?,
            checkOutDate: Long?,
            location: String
        ): Boolean {
            return name.isNotBlank() && phone.isNotBlank() && checkInDate != null && checkOutDate != null && location.isNotBlank()
        }

        fun canAdvanceFromAdditionalPage(
            reservationCode: String,
            extraInfo: String,
            additionalInfo: String
        ): Boolean {
            return reservationCode.isNotBlank() && extraInfo.isNotBlank() && additionalInfo.isNotBlank()
        }

        assertTrue(
            "Complete basic page should allow advance",
            canAdvanceFromBasicPage("Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary")
        )
        assertFalse(
            "Incomplete basic page should not allow advance",
            canAdvanceFromBasicPage("", "Tokyo", "2025-06-15", "2025-06-20", "Summary")
        )

        val testTimestamp = System.currentTimeMillis()
        assertTrue(
            "Complete accommodation page should allow advance",
            canAdvanceFromAccommodationPage(
                "Hotel",
                "123",
                testTimestamp,
                testTimestamp + 86400000,
                "Location"
            )
        )
        assertFalse(
            "Incomplete accommodation page should not allow advance",
            canAdvanceFromAccommodationPage(
                "Hotel",
                "123",
                null,
                testTimestamp + 86400000,
                "Location"
            )
        )

        assertTrue(
            "Complete additional page should allow advance",
            canAdvanceFromAdditionalPage("RES123", "Extra", "Additional")
        )
        assertFalse(
            "Incomplete additional page should not allow advance",
            canAdvanceFromAdditionalPage("", "Extra", "Additional")
        )
    }

    @Test
    fun `TravelUiState business logic works correctly`() {
        fun isUIEnabled(state: TravelUiState): Boolean {
            return state !is TravelUiState.Loading
        }

        assertTrue("Idle state should enable UI", isUIEnabled(TravelUiState.Idle))
        assertTrue("Success state should enable UI", isUIEnabled(TravelUiState.Success("data")))
        assertTrue("Error state should enable UI", isUIEnabled(TravelUiState.Error("error")))
        assertFalse("Loading state should disable UI", isUIEnabled(TravelUiState.Loading))

        val successState = TravelUiState.Success("test data")
        assertEquals("test data", successState.data)
        val errorState = TravelUiState.Error("test error")
        assertEquals("test error", errorState.message)
    }

    @Test
    fun `AutocompleteResult business logic works correctly`() {
        val result = AutocompleteResult("Tokyo Station, Japan", "ChIJL_qkFAHGGGARQQDBmQfllYI")

        assertNotNull("Address should not be null", result.address)
        assertNotNull("PlaceId should not be null", result.placeId)
        assertTrue("Address should be descriptive", result.address.length > 3)
        assertTrue("PlaceId should be valid", result.placeId.isNotBlank())

        val result1 = AutocompleteResult("Tokyo Station", "place123")
        val result2 = AutocompleteResult("Tokyo Station", "place123")
        val result3 = AutocompleteResult("Shibuya Station", "place456")

        assertEquals("Identical results should be equal", result1, result2)
        assertFalse("Different results should not be equal", result1 == result3)
        assertEquals("Equal objects should have same hash", result1.hashCode(), result2.hashCode())
    }

    @Test
    fun `date range validation business rules`() {
        fun isValidDateRange(startDate: String, endDate: String): Boolean {
            if (startDate.isBlank() || endDate.isBlank()) return false

            return try {
                val start = java.time.LocalDate.parse(startDate)
                val end = java.time.LocalDate.parse(endDate)
                !end.isBefore(start)
            } catch (e: Exception) {
                false
            }
        }

        assertTrue("Same dates should be valid", isValidDateRange("2025-06-15", "2025-06-15"))
        assertTrue("End after start should be valid", isValidDateRange("2025-06-15", "2025-06-20"))
        assertFalse(
            "End before start should be invalid",
            isValidDateRange("2025-06-20", "2025-06-15")
        )
        assertFalse("Empty dates should be invalid", isValidDateRange("", "2025-06-20"))
        assertFalse(
            "Invalid date format should be invalid",
            isValidDateRange("invalid", "2025-06-20")
        )
    }

    @Test
    fun `phone number business validation suggestions`() {
        fun currentPhoneValidation(phone: String): Boolean {
            return phone.isNotBlank()
        }

        fun improvedPhoneValidation(phone: String): Boolean {
            if (phone.isBlank()) return false

            val digitsOnly = phone.replace(Regex("[^0-9+]"), "")

            return digitsOnly.length >= 7 &&
                    (digitsOnly.startsWith("+") || digitsOnly.all { it.isDigit() })
        }

        assertTrue("Current validation accepts simple input", currentPhoneValidation("123"))
        assertTrue(
            "Current validation accepts letters",
            currentPhoneValidation("abc")
        )

        assertTrue("Valid international number", improvedPhoneValidation("+1-555-123-4567"))
        assertTrue("Valid domestic number", improvedPhoneValidation("555-123-4567"))
        assertFalse("Too short number", improvedPhoneValidation("123"))
        assertFalse("Letters only", improvedPhoneValidation("abcdefg"))
    }

    @Test
    fun `trip member count business logic`() {
        fun isValidMemberCount(totalMembers: Int): Boolean {
            return totalMembers >= 1
        }

        assertTrue("Single member trip should be valid", isValidMemberCount(1))
        assertTrue("Multiple member trip should be valid", isValidMemberCount(5))
        assertFalse("Zero members should be invalid", isValidMemberCount(0))
        assertFalse("Negative members should be invalid", isValidMemberCount(-1))
    }

    @Test
    fun `accommodation check-in check-out logic`() {
        fun isValidCheckInOut(checkIn: String, checkOut: String): Boolean {
            if (checkIn.isBlank() || checkOut.isBlank()) return false

            return try {
                if (checkIn.length == 10 && checkOut.length == 10) {
                    val checkInDate = java.time.LocalDate.parse(checkIn)
                    val checkOutDate = java.time.LocalDate.parse(checkOut)
                    !checkOutDate.isBefore(checkInDate)
                } else {
                    checkIn.isNotBlank() && checkOut.isNotBlank()
                }
            } catch (e: Exception) {
                false
            }
        }

        assertTrue(
            "Same day check-in/out should be valid",
            isValidCheckInOut("2025-06-15", "2025-06-15")
        )
        assertTrue(
            "Check-out after check-in should be valid",
            isValidCheckInOut("2025-06-15", "2025-06-20")
        )
        assertFalse(
            "Check-out before check-in should be invalid",
            isValidCheckInOut("2025-06-20", "2025-06-15")
        )
        assertFalse(
            "Empty dates should be invalid",
            isValidCheckInOut("", "2025-06-20")
        )
    }

    @Test
    fun `group code generation and validation`() {
        fun isValidGroupCode(groupCode: String): Boolean {
            return groupCode.matches(Regex("^ITN-[A-Z0-9]{5}$"))
        }

        fun formatGroupCode(code: String): String {
            return when {
                code.startsWith("ITN-") -> code
                code.length == 5 -> "ITN-$code"
                else -> code
            }
        }

        assertTrue("Standard format should be valid", isValidGroupCode("ITN-12345"))
        assertTrue("Alphanumeric should be valid", isValidGroupCode("ITN-A1B2C"))

        assertFalse("Missing prefix should be invalid", isValidGroupCode("12345"))
        assertFalse("Too short should be invalid", isValidGroupCode("ITN-123"))
        assertFalse("Too long should be invalid", isValidGroupCode("ITN-123456"))
        assertFalse("Lowercase should be invalid", isValidGroupCode("ITN-abc12"))

        assertEquals("ITN-12345", formatGroupCode("12345"))
        assertEquals("ITN-ABCDE", formatGroupCode("ITN-ABCDE"))
        assertEquals("INVALID", formatGroupCode("INVALID"))
    }

    @Test
    fun `form state management logic`() {
        data class FormState(
            val currentPage: Int = 0,
            val isBasicValid: Boolean = false,
            val isAccommodationValid: Boolean = false,
            val isAdditionalValid: Boolean = false
        )

        fun canAdvanceToNextPage(formState: FormState): Boolean {
            return when (formState.currentPage) {
                0 -> formState.isBasicValid
                1 -> formState.isAccommodationValid
                2 -> formState.isAdditionalValid
                else -> false
            }
        }

        fun canCompleteTripCreation(formState: FormState): Boolean {
            return formState.currentPage == 2 && formState.isBasicValid &&
                    formState.isAccommodationValid && formState.isAdditionalValid
        }

        val incompleteState = FormState(currentPage = 0, isBasicValid = false)
        val basicCompleteState = FormState(currentPage = 0, isBasicValid = true)
        val allCompleteState = FormState(
            currentPage = 2, isBasicValid = true,
            isAccommodationValid = true, isAdditionalValid = true
        )

        assertFalse("Incomplete basic should not advance", canAdvanceToNextPage(incompleteState))
        assertTrue("Complete basic should advance", canAdvanceToNextPage(basicCompleteState))
        assertTrue(
            "All complete should allow trip creation",
            canCompleteTripCreation(allCompleteState)
        )
        assertFalse(
            "Incomplete form should not allow trip creation",
            canCompleteTripCreation(basicCompleteState)
        )
    }

    @Test
    fun `error message formatting for user display`() {
        fun getUserFriendlyErrorMessage(error: Throwable): String {
            return when {
                error.message?.contains("network", ignoreCase = true) == true ->
                    "Please check your internet connection and try again"

                error.message?.contains("timeout", ignoreCase = true) == true ->
                    "Request timed out. Please try again"

                error.message?.contains("unauthorized", ignoreCase = true) == true ->
                    "Session expired. Please log in again"

                error.message?.contains("validation", ignoreCase = true) == true ->
                    "Please check your input and try again"

                else -> "An unexpected error occurred. Please try again"
            }
        }

        assertEquals(
            "Please check your internet connection and try again",
            getUserFriendlyErrorMessage(RuntimeException("Network error occurred"))
        )
        assertEquals(
            "Request timed out. Please try again",
            getUserFriendlyErrorMessage(RuntimeException("Request timeout"))
        )
        assertEquals(
            "Session expired. Please log in again",
            getUserFriendlyErrorMessage(RuntimeException("Unauthorized access"))
        )
        assertEquals(
            "An unexpected error occurred. Please try again",
            getUserFriendlyErrorMessage(RuntimeException("Unknown error"))
        )
    }
}