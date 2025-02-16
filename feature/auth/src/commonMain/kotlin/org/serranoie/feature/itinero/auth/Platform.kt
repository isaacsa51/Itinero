package org.serranoie.feature.itinero.auth

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform