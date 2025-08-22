package com.serranoie.itinero.core.domain.model

import org.junit.Assert.*
import org.junit.Test

class DomainModelTest {

    @Test
    fun testCreateTripCreation() {
        val accommodation = Accommodation(
            name = "Test Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo Downtown",
            mapUri = "https://maps.google.com"
        )

        val createTrip = CreateTrip(
            groupName = "Summer Adventure",
            destination = "Tokyo, Japan",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Amazing summer trip to Japan",
            accommodation = accommodation,
            reservationCode = "RES123456",
            extraInfo = "Bring passports",
            additionalInfo = "Flight details in email"
        )

        assertEquals("Summer Adventure", createTrip.groupName)
        assertEquals("Tokyo, Japan", createTrip.destination)
        assertEquals("2025-06-15", createTrip.startDate)
        assertEquals("2025-06-20", createTrip.endDate)
        assertEquals("Amazing summer trip to Japan", createTrip.summary)
        assertEquals(accommodation, createTrip.accommodation)
        assertEquals("RES123456", createTrip.reservationCode)
        assertEquals("Bring passports", createTrip.extraInfo)
        assertEquals("Flight details in email", createTrip.additionalInfo)
    }

    @Test
    fun testAccommodationCreation() {
        val accommodation = Accommodation(
            name = "Grand Hotel Tokyo",
            phone = "+81-3-1234-5678",
            checkIn = "2025-06-15T15:00:00",
            checkOut = "2025-06-20T11:00:00",
            location = "1-1-1 Ginza, Chuo-ku, Tokyo",
            mapUri = "https://maps.google.com/place123"
        )

        assertEquals("Grand Hotel Tokyo", accommodation.name)
        assertEquals("+81-3-1234-5678", accommodation.phone)
        assertEquals("2025-06-15T15:00:00", accommodation.checkIn)
        assertEquals("2025-06-20T11:00:00", accommodation.checkOut)
        assertEquals("1-1-1 Ginza, Chuo-ku, Tokyo", accommodation.location)
        assertEquals("https://maps.google.com/place123", accommodation.mapUri)
    }

    @Test
    fun testAccommodationWithNullMapUri() {
        val accommodation = Accommodation(
            name = "Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo",
            mapUri = null
        )

        assertNull(accommodation.mapUri)
        assertEquals("Hotel", accommodation.name)
    }

    @Test
    fun testTripCreation() {
        val accommodation = Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)

        val trip = Trip(
            id = "trip123",
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            totalMembers = 5,
            accommodation = accommodation,
            reservationCode = "RES123",
            extraInfo = "Extra info",
            additionalInfo = "Additional info",
            groupCode = "ITN-12345",
            ownerId = "user123"
        )

        assertEquals("trip123", trip.id)
        assertEquals("Test Trip", trip.groupName)
        assertEquals("Tokyo", trip.destination)
        assertEquals(5, trip.totalMembers)
        assertEquals("ITN-12345", trip.groupCode)
        assertEquals("user123", trip.ownerId)
    }

    @Test
    fun testDataClassCopyFunctions() {
        val originalAccommodation =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val copiedAccommodation = originalAccommodation.copy(name = "New Hotel")

        assertEquals("New Hotel", copiedAccommodation.name)
        assertEquals("123", copiedAccommodation.phone) // Unchanged
        assertEquals("Hotel", originalAccommodation.name) // Original unchanged

        val originalTrip = CreateTrip(
            "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
            originalAccommodation, "RES", "Extra", "Additional"
        )
        val copiedTrip = originalTrip.copy(groupName = "New Trip")

        assertEquals("New Trip", copiedTrip.groupName)
        assertEquals("Tokyo", copiedTrip.destination) // Unchanged
        assertEquals("Trip", originalTrip.groupName) // Original unchanged
    }

    @Test
    fun testDataClassEquality() {
        val accommodation1 =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val accommodation2 =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val accommodation3 =
            Accommodation("Different Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)

        assertEquals(accommodation1, accommodation2) // Same data
        assertNotEquals(accommodation1, accommodation3) // Different data
        assertEquals(accommodation1.hashCode(), accommodation2.hashCode()) // Same hash
    }

    @Test
    fun testValidationLogic() {
        // Test business validation rules
        fun isValidTrip(createTrip: CreateTrip): Boolean {
            return createTrip.groupName.isNotBlank() &&
                    createTrip.destination.isNotBlank() &&
                    createTrip.startDate.isNotBlank() &&
                    createTrip.endDate.isNotBlank() &&
                    createTrip.summary.isNotBlank() &&
                    createTrip.accommodation.name.isNotBlank() &&
                    createTrip.accommodation.location.isNotBlank()
        }

        val validAccommodation =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val validTrip = CreateTrip(
            "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
            validAccommodation, "RES", "Extra", "Additional"
        )

        assertTrue(isValidTrip(validTrip))

        val invalidTrip = validTrip.copy(groupName = "")
        assertFalse(isValidTrip(invalidTrip))
    }
}