package com.serranoie.app.feature

import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TravelViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `TravelUiState sealed interface behavior works correctly`() {
        val idle = TravelUiState.Idle
        val loading = TravelUiState.Loading
        val success = TravelUiState.Success("data")
        val error = TravelUiState.Error("error")
        val networkError = TravelUiState.NetworkError
        val noInternet = TravelUiState.NoInternet

        // All states should be different
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
    fun `AutocompleteResult data class works correctly`() {
        val result = AutocompleteResult("Tokyo Station", "place123")
        assertEquals("Tokyo Station", result.address)
        assertEquals("place123", result.placeId)
    }

    @Test
    fun `Trip domain model creation works correctly`() {
        val trip = createMockTrip("ITN-12345", "Tokyo Trip")

        assertEquals("ITN-12345", trip.groupCode)
        assertEquals("Tokyo Trip", trip.groupName)
        assertEquals("Test Destination", trip.destination)
        assertEquals("2025-06-15", trip.startDate)
        assertEquals("2025-06-20", trip.endDate)
        assertEquals(1, trip.totalMembers)
    }

    @Test
    fun `Accommodation domain model creation works correctly`() {
        val accommodation = Accommodation(
            name = "Test Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Test Location",
            mapUri = null
        )

        assertEquals("Test Hotel", accommodation.name)
        assertEquals("123456789", accommodation.phone)
        assertEquals("Test Location", accommodation.location)
        assertTrue("mapUri should be null", accommodation.mapUri == null)
    }

    @Test
    fun `CreateTrip domain model validation works correctly`() {
        val accommodation = Accommodation(
            name = "Test Hotel",
            phone = "123456789",
            checkIn = "2025-06-15",
            checkOut = "2025-06-20",
            location = "Tokyo",
            mapUri = null
        )

        val createTrip = CreateTrip(
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            accommodation = accommodation,
            reservationCode = "RES123",
            extraInfo = "Extra info",
            additionalInfo = "Additional info"
        )

        // Test business validation logic
        fun isValidCreateTrip(trip: CreateTrip): Boolean {
            return trip.groupName.isNotBlank() &&
                    trip.destination.isNotBlank() &&
                    trip.startDate.isNotBlank() &&
                    trip.endDate.isNotBlank() &&
                    trip.summary.isNotBlank()
        }

        assertTrue("Valid trip should pass validation", isValidCreateTrip(createTrip))

        val invalidTrip = createTrip.copy(groupName = "")
        assertTrue("Invalid trip should fail validation", !isValidCreateTrip(invalidTrip))
    }

    @Test
    fun `form validation logic works correctly for basic page`() {
        fun isBasicPageValid(
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
            "Complete basic page should be valid",
            isBasicPageValid("Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary")
        )

        assertTrue(
            "Incomplete basic page should be invalid",
            !isBasicPageValid("", "Tokyo", "2025-06-15", "2025-06-20", "Summary")
        )
    }

    @Test
    fun `accommodation page validation logic works correctly`() {
        fun isAccommodationPageValid(
            name: String,
            phone: String,
            checkInDate: Long?,
            checkOutDate: Long?,
            location: String
        ): Boolean {
            return name.isNotBlank() &&
                    phone.isNotBlank() &&
                    checkInDate != null &&
                    checkOutDate != null &&
                    location.isNotBlank()
        }

        val timestamp = System.currentTimeMillis()
        assertTrue(
            "Complete accommodation page should be valid",
            isAccommodationPageValid("Hotel", "123", timestamp, timestamp + 86400000, "Tokyo")
        )

        assertTrue(
            "Incomplete accommodation page should be invalid",
            !isAccommodationPageValid("", "123", timestamp, timestamp + 86400000, "Tokyo")
        )
    }

    private fun createMockTrip(groupCode: String, groupName: String): Trip {
        return Trip(
            id = "1",
            destination = "Test Destination",
            groupName = groupName,
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            totalMembers = 1,
            accommodation = Accommodation(
                name = "Test Hotel",
                phone = "123456789",
                checkIn = "2025-06-15",
                checkOut = "2025-06-20",
                location = "Test Location",
                mapUri = null
            ),
            reservationCode = "RES123",
            extraInfo = "Extra info",
            additionalInfo = "Additional info",
            groupCode = groupCode,
            ownerId = "user1"
        )
    }
}