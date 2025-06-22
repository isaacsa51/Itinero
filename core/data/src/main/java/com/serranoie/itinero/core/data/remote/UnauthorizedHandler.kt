package com.serranoie.itinero.core.data.remote

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Handles unauthorized (401) responses by automatically logging out the user
 * and emitting events for UI navigation
 */
object UnauthorizedHandler {
    private val _logoutEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvents: SharedFlow<Unit> = _logoutEvents.asSharedFlow()

    private var authTokenClearer: (() -> Unit)? = null

    /**
     * Sets the function to be called for clearing the authentication token during logout.
     *
     * @param clearer A function that clears the stored authentication token.
     */
    fun setAuthTokenClearer(clearer: () -> Unit) {
        authTokenClearer = clearer
    }

    /**
     * Handles an HTTP 401 Unauthorized response by logging out the user and notifying observers.
     *
     * Invokes the configured authentication token clearer, if set, and emits a logout event to notify the UI for navigation changes.
     * Any exceptions during this process are caught and logged.
     */
    fun handleUnauthorized() {
        try {
            Log.w("UnauthorizedHandler", "401 Unauthorized detected - logging out user")

            // Clear the stored token
            authTokenClearer?.invoke()

            // Emit logout event for UI to handle navigation
            _logoutEvents.tryEmit(Unit)

            Log.d("UnauthorizedHandler", "User logged out successfully")
        } catch (e: Exception) {
            Log.e("UnauthorizedHandler", "Error during automatic logout", e)
        }
    }
}
