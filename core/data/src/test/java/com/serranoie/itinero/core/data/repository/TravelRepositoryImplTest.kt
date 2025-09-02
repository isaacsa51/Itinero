package com.serranoie.itinero.core.data.repository

import com.serranoie.itinero.core.data.local.repository.LocalTravelRepository
import com.serranoie.itinero.core.data.remote.dto.trip.AccommodationDto
import com.serranoie.itinero.core.data.remote.dto.trip.CreateAccommodationDto
import com.serranoie.itinero.core.data.remote.dto.trip.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.trip.TripDto
import com.serranoie.itinero.core.data.remote.repository.TravelRepositoryImpl
import com.serranoie.itinero.core.data.remote.resources.ItineroApi
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.result.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TravelRepositoryImplTest {

    @MockK
    private lateinit var mockApi: ItineroApi

    @MockK
    private lateinit var mockLocalRepository: LocalTravelRepository

    private lateinit var travelRepository: TravelRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        travelRepository = TravelRepositoryImpl(mockApi, mockLocalRepository)
    }

    @Test
    fun `createTravel success returns CreateTrip`() = runTest {
        // Given
        val createTripRequest = CreateTrip(
            ownerId = 123,
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            accommodation = Accommodation(
                name = "Test Hotel",
                phone = "123456789",
                checkIn = "2025-06-15",
                checkOut = "2025-06-20",
                latitude = 35.6895,
                longitude = 139.6917,
                reservationCode = "ACC123",
                extraInfo = "Hotel extra info",
                location = "",
                mapUri = null
            ),
            reservationCode = "RES123",
            extraInfo = "Extra info"
        )

        val expectedResponse = CreateTripDto(
            ownerId = 123,
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            accommodation = CreateAccommodationDto(
                name = "Test Hotel",
                phone = "123456789",
                checkIn = "2025-06-15",
                checkOut = "2025-06-20",
                latitude = 35.6895,
                longitude = 139.6917,
                reservationCode = "ACC123",
                extraInfo = "Hotel extra info"
            ),
            reservationCode = "RES123",
            extraInfo = "Extra info"
        )

        coEvery { mockApi.createTrip(any()) } returns expectedResponse

        // When
        val result = travelRepository.createTravel(createTripRequest)

        // Then
        assertTrue("Expected Success result", result is Result.Success)
        val successResult = result as Result.Success
        assertEquals("Test Trip", successResult.data.groupName)
        assertEquals("Tokyo", successResult.data.destination)
    }

    @Test
    fun `getAllTravels returns empty list when no trips`() = runTest {
        // Given
        coEvery { mockApi.getAllTrips() } returns emptyList()

        // When
        val result = travelRepository.getAllTravels()

        // Then
        assertTrue("Expected Success result", result is Result.Success)
        val successResult = result as Result.Success
        assertTrue("Should return empty list", successResult.data.isEmpty())
    }

    @Test
    fun `getAllTravels returns list of trips when trips exist`() = runTest {
        // Given
        val tripDto = TripDto(
            id = "trip123",
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            totalMembers = 2,
            accommodation = AccommodationDto(
                name = "Test Hotel",
                phone = "123456789",
                checkIn = "2025-06-15",
                checkOut = "2025-06-20",
                location = "Tokyo Downtown",
                latitude = 35.6895,
                longitude = 139.6917,
                mapUri = "",
                reservationCode = "ACC123",
                extraInfo = "Hotel extra info"
            ),
            reservationCode = "RES123",
            extraInfo = "Extra info",
            groupCode = "ITN-12345",
            ownerId = "user123",
            additionalInfo = "Additional info"
        )

        coEvery { mockApi.getAllTrips() } returns listOf(tripDto)

        // When
        val result = travelRepository.getAllTravels()

        // Then
        assertTrue("Expected Success result", result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data.size)
        assertEquals("Test Trip", successResult.data.first().groupName)
        assertEquals("Tokyo", successResult.data.first().destination)
    }

    @Test
    fun `joinTravel with valid group code succeeds`() = runTest {
        // Given
        val groupCode = "ITN-12345"
        coEvery { mockApi.joinTrip(groupCode) } returns Unit
        coEvery { mockLocalRepository.clearAllTrips() } returns Result.Success(Unit)

        val tripDto = TripDto(
            id = "trip123",
            groupName = "Test Trip",
            destination = "Tokyo",
            startDate = "2025-06-15",
            endDate = "2025-06-20",
            summary = "Test summary",
            totalMembers = 2,
            accommodation = AccommodationDto(
                name = "Test Hotel",
                phone = "123456789",
                checkIn = "2025-06-15",
                checkOut = "2025-06-20",
                location = "Tokyo Downtown",
                latitude = 35.6895,
                longitude = 139.6917,
                mapUri = "",
                reservationCode = "ACC123",
                extraInfo = "Hotel extra info"
            ),
            reservationCode = "RES123",
            extraInfo = "Extra info",
            groupCode = groupCode,
            ownerId = "user123",
            additionalInfo = "Additional info"
        )
        coEvery { mockApi.getTripById(groupCode) } returns tripDto
        coEvery { mockLocalRepository.cacheTrip(any()) } returns Result.Success(Unit)

        // When
        val result = travelRepository.joinTravel(groupCode)

        // Then
        assertTrue("Expected Success result", result is Result.Success)
    }
}