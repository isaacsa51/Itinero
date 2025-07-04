package com.serranoie.itinero.core.domain.handler

/**
 * Interface for handling unauthorized responses
 */
interface UnauthorizedHandler {
    fun handleUnauthorized()
    fun setAuthTokenClearer(clearer: () -> Unit)
}

/**
 * Callback interface for unauthorized events
 */
interface UnauthorizedCallback {
    fun onUnauthorized()
}