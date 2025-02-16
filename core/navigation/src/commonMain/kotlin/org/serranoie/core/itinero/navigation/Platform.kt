package org.serranoie.core.itinero.navigation

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform