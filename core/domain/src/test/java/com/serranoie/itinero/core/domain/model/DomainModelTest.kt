package com.serranoie.itinero.core.domain.model

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

        assert(createTrip.groupName == "Summer Adventure")
        assert(createTrip.destination == "Tokyo, Japan")
        assert(createTrip.startDate == "2025-06-15")
        assert(createTrip.endDate == "2025-06-20")
        assert(createTrip.summary == "Amazing summer trip to Japan")
        assert(createTrip.accommodation == accommodation)
        assert(createTrip.reservationCode == "RES123456")
        assert(createTrip.extraInfo == "Bring passports")
        assert(createTrip.additionalInfo == "Flight details in email")
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

        assert(accommodation.name == "Grand Hotel Tokyo")
        assert(accommodation.phone == "+81-3-1234-5678")
        assert(accommodation.checkIn == "2025-06-15T15:00:00")
        assert(accommodation.checkOut == "2025-06-20T11:00:00")
        assert(accommodation.location == "1-1-1 Ginza, Chuo-ku, Tokyo")
        assert(accommodation.mapUri == "https://maps.google.com/place123")
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

        assert(accommodation.mapUri == null)
        assert(accommodation.name == "Hotel")
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

        assert(trip.id == "trip123")
        assert(trip.groupName == "Test Trip")
        assert(trip.destination == "Tokyo")
        assert(trip.totalMembers == 5)
        assert(trip.groupCode == "ITN-12345")
        assert(trip.ownerId == "user123")
    }

    @Test
    fun testDataClassCopyFunctions() {
        val originalAccommodation =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val copiedAccommodation = originalAccommodation.copy(name = "New Hotel")

        assert(copiedAccommodation.name == "New Hotel")
        assert(copiedAccommodation.phone == "123") // Unchanged
        assert(originalAccommodation.name == "Hotel") // Original unchanged

        val originalTrip = CreateTrip(
            "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
            originalAccommodation, "RES", "Extra", "Additional"
        )
        val copiedTrip = originalTrip.copy(groupName = "New Trip")

        assert(copiedTrip.groupName == "New Trip")
        assert(copiedTrip.destination == "Tokyo") // Unchanged
        assert(originalTrip.groupName == "Trip") // Original unchanged
    }

    @Test
    fun testDataClassEquality() {
        val accommodation1 =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val accommodation2 =
            Accommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)
        val accommodation3 =
            Accommodation("Different Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null)

        assert(accommodation1 == accommodation2) // Same data
        assert(accommodation1 != accommodation3) // Different data
        assert(accommodation1.hashCode() == accommodation2.hashCode()) // Same hash
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

        assert(isValidTrip(validTrip))

        val invalidTrip = validTrip.copy(groupName = "")
        assert(!isValidTrip(invalidTrip))
    }
}