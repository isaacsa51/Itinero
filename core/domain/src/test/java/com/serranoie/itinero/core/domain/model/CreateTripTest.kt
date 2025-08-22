package com.serranoie.itinero.core.domain.model

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull

class CreateTripTest {

    @Test
    fun `CreateTrip creates correctly with all fields`() {
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
    fun `CreateTrip copy function works correctly`() {
        val originalAccommodation = Accommodation(
            name = "Original Hotel",
            phone = "111111111",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo",
            mapUri = null
        )

        val original = CreateTrip(
            groupName = "Original Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Original summary",
            accommodation = originalAccommodation,
            reservationCode = "ORIG123",
            extraInfo = "Original extra",
            additionalInfo = "Original additional"
        )

        val copied = original.copy(
            groupName = "Updated Trip",
            destination = "Kyoto"
        )

        assertEquals("Updated Trip", copied.groupName)
        assertEquals("Kyoto", copied.destination)
        assertEquals("2025-06-15", copied.startDate)
        assertEquals(originalAccommodation, copied.accommodation)
    }

    @Test
    fun `CreateTrip equality works correctly`() {
        val accommodation = Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)

        val trip1 = CreateTrip(
            "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
            accommodation, "RES", "Extra", "Additional"
        )
        val trip2 = CreateTrip(
            "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
            accommodation, "RES", "Extra", "Additional"
        )
        val trip3 = CreateTrip(
            "Different Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
            accommodation, "RES", "Extra", "Additional"
        )

        assertEquals("Identical trips should be equal", trip1, trip2)
        assertNotEquals("Different trips should not be equal", trip1, trip3)
    }
}

class AccommodationTest {

    @Test
    fun `Accommodation creates correctly with all fields`() {
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
    fun `Accommodation handles null mapUri correctly`() {
        val accommodation = Accommodation(
            name = "Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo",
            mapUri = null
        )

        assertNull("mapUri should be null", accommodation.mapUri)
    }

    @Test
    fun `Accommodation copy function works correctly`() {
        val original = Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val copied = original.copy(name = "New Hotel", mapUri = "https://maps.google.com")

        assertEquals("New Hotel", copied.name)
        assertEquals("https://maps.google.com", copied.mapUri)
        assertEquals("123", copied.phone) // Should remain unchanged
        assertEquals("Tokyo", copied.location) // Should remain unchanged
    }

    @Test
    fun `Accommodation equality works correctly`() {
        val accommodation1 =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val accommodation2 =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val accommodation3 =
            Accommodation("Different Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)

        assertEquals("Identical accommodations should be equal", accommodation1, accommodation2)
        assertNotEquals(
            "Different accommodations should not be equal",
            accommodation1,
            accommodation3
        )
    }
}