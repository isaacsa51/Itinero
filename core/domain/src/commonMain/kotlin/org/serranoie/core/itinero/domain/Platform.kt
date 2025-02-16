package org.serranoie.core.itinero.domain

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform