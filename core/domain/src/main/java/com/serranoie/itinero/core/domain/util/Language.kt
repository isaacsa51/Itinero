/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: Language.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.domain.util

import java.util.*

object Language {
    private const val TAG = "LanguageDetection"

    const val ENGLISH = "en"
    const val SPANISH = "es"
    const val FRENCH = "fr"
    const val GERMAN = "de"
    const val ITALIAN = "it"
    const val PORTUGUESE = "pt"
    const val JAPANESE = "ja"
    const val CHINESE = "zh"
    const val RUSSIAN = "ru"

    val supportedLanguages = listOf(
        ENGLISH, SPANISH, FRENCH, GERMAN, ITALIAN,
        PORTUGUESE, JAPANESE, CHINESE, RUSSIAN
    )

    fun getDeviceLanguage(): String {
        val deviceLanguage = Locale.getDefault().language
        return if (supportedLanguages.contains(deviceLanguage)) {
            deviceLanguage
        } else {
            ENGLISH
        }
    }

    fun isLanguageSupported(languageCode: String): Boolean {
        return supportedLanguages.contains(languageCode)
    }


    fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            ENGLISH -> "English"
            SPANISH -> "Español"
            FRENCH -> "Français"
            GERMAN -> "Deutsch"
            ITALIAN -> "Italiano"
            PORTUGUESE -> "Português"
            JAPANESE -> "日本語"
            CHINESE -> "中文"
            RUSSIAN -> "Русский"
            else -> "English"
        }
    }
}