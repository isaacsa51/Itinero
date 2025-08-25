package com.serranoie.app.feature.model

import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.CreateTrip
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class CreateTripValidationTest {

    @Test
    fun `isValidCreateTrip returns true when all required fields are present`() {
        // Given
        val validTrip = createValidTrip()

        // When
        val isValid = isValidCreateTrip(validTrip)

        // Then
        assertTrue("Valid trip should pass validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when groupName is blank`() {
        // Given
        val invalidTrip = createValidTrip().copy(groupName = "")

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty group name should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when destination is blank`() {
        // Given
        val invalidTrip = createValidTrip().copy(destination = "")

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty destination should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when startDate is blank`() {
        // Given
        val invalidTrip = createValidTrip().copy(startDate = "")

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty start date should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when endDate is blank`() {
        // Given
        val invalidTrip = createValidTrip().copy(endDate = "")

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty end date should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when summary is blank`() {
        // Given
        val invalidTrip = createValidTrip().copy(summary = "")

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty summary should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when accommodation name is blank`() {
        // Given
        val invalidAccommodation = createValidAccommodation().copy(name = "")
        val invalidTrip = createValidTrip().copy(accommodation = invalidAccommodation)

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty accommodation name should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when accommodation phone is blank`() {
        // Given
        val invalidAccommodation = createValidAccommodation().copy(phone = "")
        val invalidTrip = createValidTrip().copy(accommodation = invalidAccommodation)

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty accommodation phone should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when accommodation location is blank`() {
        // Given
        val invalidAccommodation = createValidAccommodation().copy(location = "")
        val invalidTrip = createValidTrip().copy(accommodation = invalidAccommodation)

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty accommodation location should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when reservationCode is blank`() {
        // Given
        val invalidTrip = createValidTrip().copy(reservationCode = "")

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty reservation code should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip returns false when extraInfo is blank`() {
        // Given
        val invalidTrip = createValidTrip().copy(extraInfo = "")

        // When
        val isValid = isValidCreateTrip(invalidTrip)

        // Then
        assertFalse("Trip with empty extra info should fail validation", isValid)
    }

    @Test
    fun `isValidCreateTrip handles whitespace-only fields correctly`() {
        // Given
        val tripWithWhitespace = createValidTrip().copy(groupName = "   ")

        // When
        val isValid = isValidCreateTrip(tripWithWhitespace)

        // Then
        assertFalse("Trip with whitespace-only group name should fail validation", isValid)
    }

    private fun createValidAccommodation(): Accommodation {
        return Accommodation(
            name = "Test Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo",
            mapUri = null,
            latitude = null,
            longitude = null,
            reservationCode = null,
            extraInfo = null
        )
    }

    private fun createValidTrip(): CreateTrip {
        return CreateTrip(
            ownerId = 123,
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            accommodation = createValidAccommodation(),
            reservationCode = "RES123",
            extraInfo = "Extra info"
        )
    }

    private fun isValidCreateTrip(createTrip: CreateTrip): Boolean {
        return createTrip.groupName.isNotBlank() &&
                createTrip.destination.isNotBlank() &&
                createTrip.startDate.isNotBlank() &&
                createTrip.endDate.isNotBlank() &&
                createTrip.summary.isNotBlank() &&
                createTrip.accommodation.name.isNotBlank() &&
                createTrip.accommodation.phone.isNotBlank() &&
                createTrip.accommodation.location.isNotBlank() &&
                createTrip.reservationCode.isNotBlank() &&
                createTrip.extraInfo.isNotBlank()
    }
}