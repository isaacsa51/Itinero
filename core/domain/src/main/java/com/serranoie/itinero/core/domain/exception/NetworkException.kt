/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: NetworkException.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.domain.exception

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)