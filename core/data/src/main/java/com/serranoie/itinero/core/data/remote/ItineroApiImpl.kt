package com.serranoie.itinero.core.data.remote

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.RegisterRequestDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.domain.model.UpdateTrip
import io.ktor.client.HttpClient

class ItineroApiImpl(
    client: HttpClient
) : BaseApiClient(client), ItineroApi {

    /**
     * Authenticates a user with the provided email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return An authentication response containing user credentials and tokens.
     */
    override suspend fun loginUser(email: String, password: String): AuthResponse {
        return post("/auth/login", LoginRequestDto(email, password))
    }

    /**
     * Registers a new user with the provided details.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param name The user's first name.
     * @param surname The user's surname.
     * @param phone The user's phone number.
     * @return The authentication response containing user and token information.
     */
    override suspend fun registerUser(
        email: String, password: String, name: String, surname: String, phone: String
    ): AuthResponse {
        return post("/auth/register", RegisterRequestDto(name, surname, email, password, phone))
    }

    /**
     * Logs out the current user by sending a logout request to the server.
     */
    override suspend fun logoutUser() {
        post<Unit, Unit>("/auth/logout")
    }

    /**
     * Initiates the password reset process for the user with the specified email address.
     *
     * Sends a request to the server to trigger a password reset email.
     *
     * @param email The email address of the user requesting a password reset.
     */
    override suspend fun forgotPasswordUser(email: String) {
        val body = mapOf("email" to email)
        post<Unit, Map<String, String>>("/auth/forgot-password", body)
    }

    /**
     * Retrieves a trip by its unique identifier.
     *
     * @param id The unique identifier of the trip to retrieve.
     * @return The trip data corresponding to the specified ID.
     */
    override suspend fun getTripById(id: String): TripDto {
        return get("/trips/$id")
    }

    /**
     * Retrieves a list of all trips.
     *
     * @return A list of `TripDto` objects representing all available trips.
     */
    override suspend fun getAllTrips(): List<TripDto> {
        return get("/trips")
    }

    /**
     * Creates a new trip with the provided details.
     *
     * @param request The trip information to create.
     * @return The created trip details.
     */
    override suspend fun createTrip(request: CreateTripDto): CreateTripDto {
        return post("/trips/new", request)
    }

    /**
     * Updates the information of a trip identified by the given group code.
     *
     * @param groupCode The unique code of the trip group to update.
     * @param request The updated trip information.
     */
    override suspend fun updateTripInfo(groupCode: String, request: UpdateTrip) {
        put<Unit, UpdateTrip>("/trips/$groupCode/info", request)
    }

    /****
     * Joins the trip associated with the specified group code.
     *
     * @param groupCode The unique code identifying the trip to join.
     */
    override suspend fun joinTrip(groupCode: String) {
        post<Unit, Unit>("/trips/$groupCode/join")
    }

    /**
     * Placeholder for deleting a trip.
     *
     * This method is not yet implemented and will be completed when the corresponding API endpoint becomes available.
     */
    override suspend fun deleteTrip() {
        // TODO: Implement when the API endpoint is ready
    }

    override suspend fun leaveTrip() {
        // TODO: Implement when the API endpoint is ready
    }
}
