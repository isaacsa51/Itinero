package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.local.entity.EmbeddedAccommodation
import com.serranoie.itinero.core.data.local.entity.TripEntity
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.Trip
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class MappersTest {

    @Test
    fun `TripEntity maps to Trip domain model correctly`() {
        // Given
        val embeddedAccommodation = EmbeddedAccommodation(
            name = "Test Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo Downtown",
            mapUri = "https://maps.google.com",
            latitude = 35.6762,
            longitude = 139.6503
        )

        val tripEntity = TripEntity(
            id = "trip123",
            groupName = "Summer Trip",
            destination = "Tokyo, Japan",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Amazing summer adventure",
            totalMembers = 4,
            accommodation = embeddedAccommodation,
            reservationCode = "RES789",
            extraInfo = "Don't forget passports",
            additionalInfo = "Check flight times",
            groupCode = "ITN-12345",
            ownerId = "user456"
        )

        // When
        val domainTrip = tripEntity.toDomain()

        // Then
        assertEquals("trip123", domainTrip.id)
        assertEquals("Summer Trip", domainTrip.groupName)
        assertEquals("Tokyo, Japan", domainTrip.destination)
        assertEquals("2025-06-15", domainTrip.startDate)
        assertEquals("2025-06-20", domainTrip.endDate)
        assertEquals("Amazing summer adventure", domainTrip.summary)
        assertEquals(4, domainTrip.totalMembers)
        assertEquals("RES789", domainTrip.reservationCode)
        assertEquals("Don't forget passports", domainTrip.extraInfo)
        assertEquals("Check flight times", domainTrip.additionalInfo)
        assertEquals("ITN-12345", domainTrip.groupCode)
        assertEquals("user456", domainTrip.ownerId)

        // Test accommodation mapping
        assertEquals("Test Hotel", domainTrip.accommodation.name)
        assertEquals("123456789", domainTrip.accommodation.phone)
        assertEquals("2025-06-15", domainTrip.accommodation.checkIn)
        assertEquals("2025-06-20", domainTrip.accommodation.checkOut)
        assertEquals("Tokyo Downtown", domainTrip.accommodation.location)
        assertEquals("https://maps.google.com", domainTrip.accommodation.mapUri)
        assertEquals(35.6762, domainTrip.accommodation.latitude!!, 0.001)
        assertEquals(139.6503, domainTrip.accommodation.longitude!!, 0.001)
    }

    @Test
    fun `EmbeddedAccommodation maps to Accommodation correctly`() {
        // Given
        val embeddedAccommodation = EmbeddedAccommodation(
            name = "Luxury Hotel",
            phone = "+81-3-5555-1234",
            checkIn = "2025-07-01T15:00",
            checkOut = "2025-07-05T11:00",
            location = "Shibuya, Tokyo",
            mapUri = null,
            latitude = 35.6647,
            longitude = 139.6992
        )

        // When
        val domainAccommodation = embeddedAccommodation.toDomain()

        // Then
        assertEquals("Luxury Hotel", domainAccommodation.name)
        assertEquals("+81-3-5555-1234", domainAccommodation.phone)
        assertEquals("2025-07-01T15:00", domainAccommodation.checkIn)
        assertEquals("2025-07-05T11:00", domainAccommodation.checkOut)
        assertEquals("Shibuya, Tokyo", domainAccommodation.location)
        assertNull("mapUri should be null", domainAccommodation.mapUri)
        assertEquals(35.6647, domainAccommodation.latitude!!, 0.001)
        assertEquals(139.6992, domainAccommodation.longitude!!, 0.001)
    }

    @Test
    fun `Trip maps to TripEntity correctly`() {
        // Given
        val domainAccommodation = Accommodation(
            name = "Business Hotel",
            phone = "987654321",
            checkIn = "2025-09-10",
            checkOut = "2025-09-15",
            location = "Osaka",
            mapUri = "https://business-hotel.com/map",
            latitude = 34.6937,
            longitude = 135.5023,
            reservationCode = "BUS123",
            extraInfo = "Formal attire required",
        )

        val domainTrip = Trip(
            id = "business123",
            groupName = "Business Trip",
            destination = "Osaka, Japan",
            startDate = "2025-09-10",
            endDate = "2025-09-15",
            summary = "Important business meetings",
            totalMembers = 2,
            accommodation = domainAccommodation,
            reservationCode = "BUS456",
            extraInfo = "Formal attire required",
            additionalInfo = "Meeting schedule attached",
            groupCode = "ITN-67890",
            ownerId = "manager789"
        )

        // When
        val entityTrip = domainTrip.toEntity()

        // Then
        assertEquals("business123", entityTrip.id)
        assertEquals("Business Trip", entityTrip.groupName)
        assertEquals("Osaka, Japan", entityTrip.destination)
        assertEquals("2025-09-10", entityTrip.startDate)
        assertEquals("2025-09-15", entityTrip.endDate)
        assertEquals("Important business meetings", entityTrip.summary)
        assertEquals(2, entityTrip.totalMembers)
        assertEquals("BUS456", entityTrip.reservationCode)
        assertEquals("Formal attire required", entityTrip.extraInfo)
        assertEquals("Meeting schedule attached", entityTrip.additionalInfo)
        assertEquals("ITN-67890", entityTrip.groupCode)
        assertEquals("manager789", entityTrip.ownerId)

        // Test embedded accommodation mapping
        assertEquals("Business Hotel", entityTrip.accommodation.name)
        assertEquals("987654321", entityTrip.accommodation.phone)
        assertEquals("2025-09-10", entityTrip.accommodation.checkIn)
        assertEquals("2025-09-15", entityTrip.accommodation.checkOut)
        assertEquals("Osaka", entityTrip.accommodation.location)
        assertEquals("https://business-hotel.com/map", entityTrip.accommodation.mapUri)
        assertEquals(34.6937, entityTrip.accommodation.latitude!!, 0.001)
        assertEquals(135.5023, entityTrip.accommodation.longitude!!, 0.001)
    }

    @Test
    fun `round trip mapping preserves data integrity`() {
        // Given
        val originalEmbeddedAccommodation = EmbeddedAccommodation(
            name = "Round Trip Hotel",
            phone = "111222333",
            checkIn = "2025-12-01",
            checkOut = "2025-12-05",
            location = "Kyoto",
            mapUri = "https://kyoto-hotel.jp",
            latitude = 35.0211,
            longitude = 135.7556
        )

        val originalEntity = TripEntity(
            id = "round123",
            groupName = "Round Trip Test",
            destination = "Kyoto",
            startDate = "2025-12-01",
            endDate = "2025-12-05",
            summary = "Testing round trip mapping",
            totalMembers = 3,
            accommodation = originalEmbeddedAccommodation,
            reservationCode = "ROUND789",
            extraInfo = "Test extra info",
            additionalInfo = "Test additional info",
            groupCode = "ITN-ROUND",
            ownerId = "tester123"
        )

        // When - convert to domain and back to entity
        val domainTrip = originalEntity.toDomain()
        val backToEntity = domainTrip.toEntity()

        // Then - should be identical (except for ID which might be regenerated)
        assertEquals(
            "Group name should be preserved",
            originalEntity.groupName,
            backToEntity.groupName
        )
        assertEquals(
            "Destination should be preserved",
            originalEntity.destination,
            backToEntity.destination
        )
        assertEquals(
            "Accommodation should be preserved",
            originalEntity.accommodation,
            backToEntity.accommodation
        )
        assertEquals(
            "Group code should be preserved",
            originalEntity.groupCode,
            backToEntity.groupCode
        )
    }

    @Test
    fun `mapper handles null mapUri correctly`() {
        // Given
        val accommodationWithNullUri = EmbeddedAccommodation(
            name = "No Map Hotel",
            phone = "555666777",
            checkIn = "2025-10-10",
            checkOut = "2025-10-15",
            location = "Remote Location",
            mapUri = null,
            latitude = 40.7128,
            longitude = -74.0060
        )

        // When
        val domainAccommodation = accommodationWithNullUri.toDomain()

        // Then
        assertNull("mapUri should remain null", domainAccommodation.mapUri)
        assertEquals("No Map Hotel", domainAccommodation.name)
        assertEquals(40.7128, domainAccommodation.latitude!!, 0.001)
        assertEquals(-74.0060, domainAccommodation.longitude!!, 0.001)
    }

    @Test
    fun `Accommodation toEmbedded mapping works correctly`() {
        // Given
        val domainAccommodation = Accommodation(
            name = "Convert Test Hotel",
            phone = "999888777",
            checkIn = "2025-11-01",
            checkOut = "2025-11-05",
            location = "Test City",
            mapUri = "https://test-hotel.com",
            latitude = 51.5074,
            longitude = -0.1278,
            reservationCode = "RES123",
            extraInfo = "Test extra info"
        )

        // When
        val embeddedAccommodation = domainAccommodation.toEmbedded()

        // Then
        assertEquals("Convert Test Hotel", embeddedAccommodation.name)
        assertEquals("999888777", embeddedAccommodation.phone)
        assertEquals("2025-11-01", embeddedAccommodation.checkIn)
        assertEquals("2025-11-05", embeddedAccommodation.checkOut)
        assertEquals("Test City", embeddedAccommodation.location)
        assertEquals("https://test-hotel.com", embeddedAccommodation.mapUri)
        assertEquals(51.5074, embeddedAccommodation.latitude!!, 0.001)
        assertEquals(-0.1278, embeddedAccommodation.longitude!!, 0.001)
    }
}