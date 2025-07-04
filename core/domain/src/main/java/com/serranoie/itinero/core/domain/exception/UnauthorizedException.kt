package com.serranoie.itinero.core.domain.exception

/**
 * Exception thrown when a 401 Unauthorized response is received
 */
class UnauthorizedException(message: String) : Exception(message)