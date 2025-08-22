package com.serranoie.app.feature

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class TravelViewModelStateTest {

    @Test
    fun `TravelUiState transitions work correctly`() {
        val idleState = TravelUiState.Idle
        val loadingState = TravelUiState.Loading
        val successState = TravelUiState.Success("test data")
        val errorState = TravelUiState.Error("test error")
        val networkErrorState = TravelUiState.NetworkError
        val noInternetState = TravelUiState.NoInternet

        assertTrue("Idle and Loading should be different", idleState != loadingState)
        assertTrue("Loading and Success should be different", loadingState != successState)
        assertTrue("Success and Error should be different", successState != errorState)
        assertTrue("Error and NetworkError should be different", errorState != networkErrorState)
        assertTrue(
            "NetworkError and NoInternet should be different",
            networkErrorState != noInternetState
        )
    }

    @Test
    fun `TravelUiState Success generic type works correctly`() {
        val stringSuccess = TravelUiState.Success("string data")
        val intSuccess = TravelUiState.Success(42)
        val listSuccess = TravelUiState.Success(listOf("item1", "item2"))

        assertEquals("string data", stringSuccess.data)
        assertEquals(42, intSuccess.data)
        assertEquals(listOf("item1", "item2"), listSuccess.data)
    }

    @Test
    fun `AutocompleteResult data class properties work correctly`() {
        val result = AutocompleteResult(
            address = "1600 Amphitheatre Parkway, Mountain View, CA, USA",
            placeId = "ChIJj61dQgK6j4AR4GeTYWZsKWw"
        )

        assertEquals("1600 Amphitheatre Parkway, Mountain View, CA, USA", result.address)
        assertEquals("ChIJj61dQgK6j4AR4GeTYWZsKWw", result.placeId)
    }

    @Test
    fun `AutocompleteResult copy function works correctly`() {
        val original = AutocompleteResult("Original Address", "original_id")
        val copied = original.copy(address = "New Address")

        assertEquals("New Address", copied.address)
        assertEquals("original_id", copied.placeId)
        assertTrue("Original and copy should be different objects", original != copied)
    }

    @Test
    fun `AutocompleteResult component functions work correctly`() {
        val result = AutocompleteResult("Test Address", "test_id")

        val (address, placeId) = result
        assertEquals("Test Address", address)
        assertEquals("test_id", placeId)
    }
}