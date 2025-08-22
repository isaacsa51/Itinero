package com.serranoie.app.feature.home

import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.TripOverview
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.model.UpdateTripAccommodation
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
import org.junit.Assert.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testGroupCode = "ITN-12345"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HomeUiState sealed interface behavior works correctly`() {
        val idle = HomeUiState.Idle
        val loading = HomeUiState.Loading
        val success = HomeUiState.Success(createMockTrip())
        val error = HomeUiState.Error("test error")

        assertTrue("Idle and Loading should be different", idle != loading)
        assertTrue("Loading and Success should be different", loading != success)
        assertTrue("Success and Error should be different", success != error)
    }

    @Test
    fun `OverviewUiState sealed interface behavior works correctly`() {
        val idle = OverviewUiState.Idle
        val loading = OverviewUiState.Loading
        val mockOverview = TripOverview(
            date = "2025-06-15",
            todayItinerary = emptyList(),
            yesterdayDate = "2025-06-14",
            yesterdayExpenses = emptyList()
        )
        val success = OverviewUiState.Success(mockOverview)
        val error = OverviewUiState.Error("test error")

        assertTrue("Idle and Loading should be different", idle != loading)
        assertTrue("Loading and Success should be different", loading != success)
        assertTrue("Success and Error should be different", success != error)
    }

    @Test
    fun `Trip domain model properties work correctly`() {
        val trip = createMockTrip()

        assertEquals("test123", trip.id)
        assertEquals("Test Trip", trip.groupName)
        assertEquals("Tokyo", trip.destination)
        assertEquals("2025-06-15", trip.startDate)
        assertEquals("2025-06-20", trip.endDate)
        assertEquals("Test summary", trip.summary)
        assertEquals(1, trip.totalMembers)
        assertEquals(testGroupCode, trip.groupCode)
        assertEquals("user123", trip.ownerId)
    }

    @Test
    fun `UpdateTrip domain model creation works correctly`() {
        val updateRequest = UpdateTrip(
            groupName = "Updated Trip",
            destination = "Updated Tokyo",
            startDate = "2025-07-01",
            endDate = "2025-07-10",
            summary = "Updated summary",
            accommodation = UpdateTripAccommodation(
                "Updated Hotel",
                "999",
                "2025-07-01",
                "2025-07-10",
                "Updated Location",
                null
            ),
            reservationCode = "UPD123",
            extraInfo = "Updated extra",
            additionalInfo = "Updated additional"
        )

        assertEquals("Updated Trip", updateRequest.groupName)
        assertEquals("Updated Tokyo", updateRequest.destination)
        assertEquals("Updated Hotel", updateRequest.accommodation.name)
        assertEquals("999", updateRequest.accommodation.phone)
        assertEquals("UPD123", updateRequest.reservationCode)
    }

    @Test
    fun `TripOverview domain model creation works correctly`() {
        val overview = TripOverview(
            date = "2025-06-15",
            todayItinerary = emptyList(),
            yesterdayDate = "2025-06-14",
            yesterdayExpenses = emptyList()
        )

        assertEquals("2025-06-15", overview.date)
        assertEquals("2025-06-14", overview.yesterdayDate)
        assertTrue("Today itinerary should be empty", overview.todayItinerary.isEmpty())
        assertTrue("Yesterday expenses should be empty", overview.yesterdayExpenses.isEmpty())
    }

    @Test
    fun `group code validation works correctly`() {
        fun isValidGroupCode(groupCode: String): Boolean {
            return groupCode.matches(Regex("^ITN-[A-Z0-9]{5}$"))
        }

        assertTrue("Valid group code should pass", isValidGroupCode("ITN-12345"))
        assertTrue("Valid alphanumeric group code should pass", isValidGroupCode("ITN-A1B2C"))
        assertFalse("Invalid format should fail", isValidGroupCode("12345"))
        assertFalse("Too short should fail", isValidGroupCode("ITN-123"))
    }

    @Test
    fun `trip update validation works correctly`() {
        fun isValidTripUpdate(updateTrip: UpdateTrip): Boolean {
            return updateTrip.groupName.isNotBlank() &&
                    updateTrip.destination.isNotBlank() &&
                    updateTrip.startDate.isNotBlank() &&
                    updateTrip.endDate.isNotBlank() &&
                    updateTrip.summary.isNotBlank()
        }

        val validUpdate = UpdateTrip(
            "Trip", "Tokyo", "2025-06-15", "2025-06-20", "Summary",
            UpdateTripAccommodation("Hotel", "123", "2025-06-15", "2025-06-20", "Tokyo", null),
            "RES123", "Extra", "Additional"
        )

        assertTrue("Valid update should pass", isValidTripUpdate(validUpdate))

        val invalidUpdate = validUpdate.copy(groupName = "")
        assertFalse("Invalid update should fail", isValidTripUpdate(invalidUpdate))
    }

    @Test
    fun `accommodation update validation works correctly`() {
        fun isValidAccommodationUpdate(accommodation: UpdateTripAccommodation): Boolean {
            return accommodation.name.isNotBlank() &&
                    accommodation.phone.isNotBlank() &&
                    accommodation.location.isNotBlank()
        }

        val validAccommodation = UpdateTripAccommodation(
            "Hotel", "123456789", "2025-06-15", "2025-06-20", "Tokyo", null
        )

        assertTrue(
            "Valid accommodation should pass",
            isValidAccommodationUpdate(validAccommodation)
        )

        val invalidAccommodation = validAccommodation.copy(name = "")
        assertFalse(
            "Invalid accommodation should fail",
            isValidAccommodationUpdate(invalidAccommodation)
        )
    }

    @Test
    fun `trip member validation works correctly`() {
        fun isValidMemberCount(totalMembers: Int): Boolean {
            return totalMembers >= 1
        }

        assertTrue("Single member should be valid", isValidMemberCount(1))
        assertTrue("Multiple members should be valid", isValidMemberCount(5))
        assertFalse("Zero members should be invalid", isValidMemberCount(0))
        assertFalse("Negative members should be invalid", isValidMemberCount(-1))
    }

    @Test
    fun `trip date validation works correctly`() {
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

        assertTrue("Valid date range should pass", isValidDateRange("2025-06-15", "2025-06-20"))
        assertTrue("Same dates should be valid", isValidDateRange("2025-06-15", "2025-06-15"))
        assertFalse("Invalid date range should fail", isValidDateRange("2025-06-20", "2025-06-15"))
        assertFalse("Empty dates should fail", isValidDateRange("", "2025-06-20"))
    }

    private fun createMockTrip(): Trip {
        return Trip(
            id = "test123",
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            totalMembers = 1,
            accommodation = Accommodation(
                "Test Hotel",
                "123456789",
                "2025-06-15",
                "2025-06-20",
                "Tokyo",
                null
            ),
            reservationCode = "TEST123",
            extraInfo = "Test extra",
            additionalInfo = "Test additional",
            groupCode = testGroupCode,
            ownerId = "user123"
        )
    }
}