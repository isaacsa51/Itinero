package org.serranoie.app.itinero

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform