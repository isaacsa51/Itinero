package com.serranoie.itinero.core.domain.model

enum class DeviceType(val value: String) {
    ANDROID("android"),
    IOS("ios"),
    WEB("web");
    
    companion object {
        fun fromString(value: String): DeviceType {
            return entries.find { it.value == value.lowercase() } ?: ANDROID
        }
    }
}