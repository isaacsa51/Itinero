/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UnauthorizedHandler.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.data.remote.resources

import android.util.Log
import com.serranoie.itinero.core.domain.usecase.LogoutObserverUseCase

/**
 * Handles unauthorized (401) responses by automatically logging out the user
 * and emitting events for UI navigation
 */
object UnauthorizedHandler {
    private var authTokenClearer: (() -> Unit)? = null
    private var logoutObserver: LogoutObserverUseCase? = null

    fun setAuthTokenClearer(clearer: () -> Unit) {
        authTokenClearer = clearer
    }

    fun setLogoutObserver(observer: LogoutObserverUseCase) {
        logoutObserver = observer
    }

    fun handleUnauthorized() {
        try {
            Log.e("UnauthorizedHandler", "401 Unauthorized detected - logging out user")

            authTokenClearer?.invoke()
            logoutObserver?.emitLogoutEvent()

        } catch (e: Exception) {
            Log.e("UnauthorizedHandler", "Error during automatic logout", e)
        }
    }
}
