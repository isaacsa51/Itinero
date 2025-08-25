package com.serranoie.app.feature.model

import com.serranoie.itinero.core.domain.model.Accommodation
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class AccommodationBusinessRulesTest {

    @Test
    fun `processMapUri returns original URI when not blank`() {
        // Given
        val validUri = "https://maps.google.com/place123"

        // When
        val result = processMapUri(validUri)

        // Then
        assertEquals("Valid URI should be preserved", validUri, result)
    }

    @Test
    fun `processMapUri returns null when URI is empty`() {
        // Given
        val emptyUri = ""

        // When
        val result = processMapUri(emptyUri)

        // Then
        assertNull("Empty URI should become null", result)
    }

    @Test
    fun `processMapUri returns null when URI is whitespace only`() {
        // Given
        val whitespaceUri = "   "

        // When
        val result = processMapUri(whitespaceUri)

        // Then
        assertNull("Whitespace-only URI should become null", result)
    }

    @Test
    fun `processMapUri preserves valid URIs with different formats`() {
        // Test various valid URI formats
        val testCases = listOf(
            "https://maps.google.com",
            "https://www.google.com/maps/place/Tokyo",
            "http://example.com/map",
            "maps://place123",
            "geo:35.6762,139.6503"
        )

        testCases.forEach { uri ->
            assertEquals("URI $uri should be preserved", uri, processMapUri(uri))
        }
    }

    @Test
    fun `isValidCheckInCheckOut returns true when check-out is after check-in`() {
        // Given
        val checkIn = "2025-06-15"
        val checkOut = "2025-06-20"

        // When
        val isValid = isValidCheckInCheckOut(checkIn, checkOut)

        // Then
        assertTrue("Check-out after check-in should be valid", isValid)
    }

    @Test
    fun `isValidCheckInCheckOut returns true when check-in and check-out are same day`() {
        // Given
        val checkIn = "2025-06-15"
        val checkOut = "2025-06-15"

        // When
        val isValid = isValidCheckInCheckOut(checkIn, checkOut)

        // Then
        assertTrue("Same day check-in/out should be valid", isValid)
    }

    @Test
    fun `isValidCheckInCheckOut returns false when check-out is before check-in`() {
        // Given
        val checkIn = "2025-06-20"
        val checkOut = "2025-06-15"

        // When
        val isValid = isValidCheckInCheckOut(checkIn, checkOut)

        // Then
        assertFalse("Check-out before check-in should be invalid", isValid)
    }

    @Test
    fun `isValidCheckInCheckOut returns false when dates are empty`() {
        // Test empty date scenarios
        assertFalse("Empty check-in should be invalid", isValidCheckInCheckOut("", "2025-06-20"))
        assertFalse("Empty check-out should be invalid", isValidCheckInCheckOut("2025-06-15", ""))
        assertFalse("Both empty should be invalid", isValidCheckInCheckOut("", ""))
    }

    @Test
    fun `isValidCheckInCheckOut handles invalid date formats gracefully`() {
        // Given
        val invalidCheckIn = "invalid-date"
        val validCheckOut = "2025-06-20"

        // When
        val isValid = isValidCheckInCheckOut(invalidCheckIn, validCheckOut)

        // Then
        assertFalse("Invalid date format should be handled gracefully", isValid)
    }

    @Test
    fun `Accommodation data class properties work correctly`() {
        // Given
        val accommodation = Accommodation(
            name = "Grand Hotel",
            phone = "+81-3-1234-5678",
            checkIn = "2025-06-15T15:00",
            checkOut = "2025-06-20T11:00",
            location = "Ginza, Tokyo",
            mapUri = "https://grand-hotel.jp/map",
            latitude = 35.6762,
            longitude = 139.6503,
            reservationCode = "GH123",
            extraInfo = "5-star hotel"
        )

        // Then
        assertEquals("Grand Hotel", accommodation.name)
        assertEquals("+81-3-1234-5678", accommodation.phone)
        assertEquals("2025-06-15T15:00", accommodation.checkIn)
        assertEquals("2025-06-20T11:00", accommodation.checkOut)
        assertEquals("Ginza, Tokyo", accommodation.location)
        assertEquals("https://grand-hotel.jp/map", accommodation.mapUri)
    }

    @Test
    fun `Accommodation copy function works correctly`() {
        // Given
        val original = Accommodation(
            name = "Hotel",
            phone = "123",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo",
            mapUri = null,
            latitude = null,
            longitude = null,
            reservationCode = null,
            extraInfo = null
        )

        // When
        val copied = original.copy(name = "New Hotel", mapUri = "https://new-hotel.com")

        // Then
        assertEquals("New Hotel", copied.name)
        assertEquals("https://new-hotel.com", copied.mapUri)
        assertEquals("123", copied.phone)
        assertEquals("Tokyo", copied.location)
    }

    private fun processMapUri(mapUri: String): String? {
        return mapUri.takeIf { it.isNotBlank() }
    }

    private fun isValidCheckInCheckOut(checkIn: String, checkOut: String): Boolean {
        if (checkIn.isBlank() || checkOut.isBlank()) return false

        return try {
            // For date-only strings (yyyy-MM-dd format)
            if (checkIn.length == 10 && checkOut.length == 10) {
                val checkInDate = java.time.LocalDate.parse(checkIn)
                val checkOutDate = java.time.LocalDate.parse(checkOut)
                !checkOutDate.isBefore(checkInDate)
            } else {
                // For datetime strings, try parsing and validate
                val checkInDate = java.time.LocalDate.parse(checkIn.substring(0, 10))
                val checkOutDate = java.time.LocalDate.parse(checkOut.substring(0, 10))
                !checkOutDate.isBefore(checkInDate)
            }
        } catch (e: Exception) {
            false
        }
    }
}