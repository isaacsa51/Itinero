package com.serranoie.core.settings

enum class ThemeMode {
    Light,
    Dark,
    Auto
}

fun String.toThemeMode(): ThemeMode {
    return when (this) {
        "Light" -> ThemeMode.Light
        "Dark" -> ThemeMode.Dark
        "System Default" -> ThemeMode.Auto
        else -> ThemeMode.Auto
    }
}

fun ThemeMode.toStringValue(): String {
    return when (this) {
        ThemeMode.Light -> "Light"
        ThemeMode.Dark -> "Dark"
        ThemeMode.Auto -> "System Default"
    }
}