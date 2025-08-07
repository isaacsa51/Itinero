/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LogoutObserverUseCase.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.domain.usecase

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Use case for observing logout events triggered by unauthorized responses
 */
class LogoutObserverUseCase {
    private val _logoutEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvents: SharedFlow<Unit> = _logoutEvents.asSharedFlow()

    fun emitLogoutEvent() {
        _logoutEvents.tryEmit(Unit)
    }
}