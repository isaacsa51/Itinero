/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ChatApiException.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 08 January 2025
 */

package com.serranoie.itinero.core.domain.exception

/**
 * Exception thrown when chat API operations fail
 */
class ChatApiException(message: String, cause: Throwable? = null) : Exception(message, cause)