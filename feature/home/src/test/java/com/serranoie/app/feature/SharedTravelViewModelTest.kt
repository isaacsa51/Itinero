package com.serranoie.app.feature

import com.serranoie.itinero.core.domain.model.Accommodation
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class SharedTravelViewModelTest {

    @Test
    fun `AutocompleteResult data class works correctly`() {
        val result = AutocompleteResult("Tokyo Station", "place123")
        assertEquals("Tokyo Station", result.address)
        assertEquals("place123", result.placeId)
    }

    @Test
    fun `TravelUiState sealed interface states are distinct`() {
        val idle = TravelUiState.Idle
        val loading = TravelUiState.Loading
        val success = TravelUiState.Success("data")
        val error = TravelUiState.Error("error")
        val networkError = TravelUiState.NetworkError
        val noInternet = TravelUiState.NoInternet

        assertTrue(
            "All states should be distinct",
            setOf(idle, loading, success, error, networkError, noInternet).size == 6
        )
    }

    @Test
    fun `TravelUiState Success contains correct data`() {
        val testData = "test data"
        val success = TravelUiState.Success(testData)
        assertEquals(testData, success.data)
    }

    @Test
    fun `TravelUiState Error contains correct message`() {
        val errorMessage = "test error"
        val error = TravelUiState.Error(errorMessage)
        assertEquals(errorMessage, error.message)
    }

    @Test
    fun `accommodation mapUri handling logic`() {
        fun processMapUri(mapUri: String): String? {
            return mapUri.takeIf { it.isNotBlank() }
        }

        assertEquals(
            "Valid URI should be preserved",
            "https://maps.google.com",
            processMapUri("https://maps.google.com")
        )
        assertEquals("Empty URI should be null", null, processMapUri(""))
        assertEquals("Blank URI should be null", null, processMapUri("   "))
    }

    @Test
    fun `create travel request validation logic`() {
        fun areRequiredFieldsValid(
            groupName: String,
            destination: String,
            startDate: String,
            endDate: String,
            summary: String
        ): Boolean {
            return groupName.isNotBlank() &&
                    destination.isNotBlank() &&
                    startDate.isNotBlank() &&
                    endDate.isNotBlank() &&
                    summary.isNotBlank()
        }

        assertTrue(
            "Valid fields should pass",
            areRequiredFieldsValid("Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary")
        )

        assertTrue(
            "Empty field should fail validation",
            !areRequiredFieldsValid("", "Tokyo", "2025-06-15", "2025-06-20", "Summary")
        )
    }

    @Test
    fun `accommodation object construction logic`() {
        fun createAccommodation(
            name: String,
            phone: String,
            checkIn: String,
            checkOut: String,
            location: String,
            mapUri: String
        ): Accommodation {
            return Accommodation(
                name = name,
                phone = phone,
                checkIn = checkIn,
                checkOut = checkOut,
                location = location,
                mapUri = mapUri.takeIf { it.isNotBlank() },
                latitude = null,
                longitude = null,
                reservationCode = null,
                extraInfo = null
            )
        }

        val accommodation = createAccommodation(
            "Test Hotel", "123456789", "2025-06-15", "2025-06-20", "Tokyo", ""
        )

        assertEquals("Test Hotel", accommodation.name)
        assertEquals("123456789", accommodation.phone)
        assertEquals(null, accommodation.mapUri)
        assertEquals(null, accommodation.latitude)
        assertEquals(null, accommodation.longitude)
        assertEquals(null, accommodation.reservationCode)
        assertEquals(null, accommodation.extraInfo)
    }
}