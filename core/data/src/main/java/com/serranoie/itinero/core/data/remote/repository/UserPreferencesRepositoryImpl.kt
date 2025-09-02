/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UserPreferencesRepositoryImpl.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.itinero.core.data.remote.repository

import android.util.Log
import com.serranoie.itinero.core.data.remote.dto.auth.UpdateLanguageRequest
import com.serranoie.itinero.core.data.remote.resources.ItineroApi
import com.serranoie.itinero.core.domain.manager.LanguageRefreshManager
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.UserPreferencesRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.util.Language
import java.util.*

class UserPreferencesRepositoryImpl(
    private val api: ItineroApi,
    private val authPreferencesRepository: AuthPreferencesRepository,
    private val languageRefreshManager: LanguageRefreshManager
) : UserPreferencesRepository {

    companion object {
        private const val TAG = "LanguageNotifications"
    }

    override suspend fun getUserLanguagePreference(): Result<String> {
        Log.d(TAG, "🔍 Getting user language preference for notifications...")

        return try {
            Log.d(TAG, "📡 Calling server API to get user preferences")
            val response = api.getUserPreferences()

            if (response.success) {
                Log.d(TAG, "✅ Server returned language: '${response.language}' for notifications")
                Log.d(TAG, "📋 Supported languages from server: ${response.supportedLanguages}")

                authPreferencesRepository.saveLanguagePreference(response.language)
                Log.d(TAG, "💾 Saved language '${response.language}' to local cache")

                Result.Success(response.language)
            } else {
                Log.w(TAG, "⚠️ Server API failed, falling back to local/device language")

                val localLanguage = authPreferencesRepository.getLanguagePreference()
                if (localLanguage != null) {
                    Log.d(TAG, "📱 Using cached language: '$localLanguage' for notifications")
                    Result.Success(localLanguage)
                } else {
                    val deviceLang = getDeviceLanguageWithLogging()
                    Log.d(TAG, "🔄 No cached language, using device language: '$deviceLang'")
                    Result.Success(deviceLang)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 API call failed: ${e.message}")

            val localLanguage = authPreferencesRepository.getLanguagePreference()
            if (localLanguage != null) {
                Log.d(TAG, "📱 Exception fallback: Using cached language '$localLanguage'")
                Result.Success(localLanguage)
            } else {
                val deviceLang = getDeviceLanguageWithLogging()
                Log.w(
                    TAG,
                    "🔄 Exception fallback: Using device language '$deviceLang' for notifications"
                )
                Result.Success(deviceLang)
            }
        }
    }

    override suspend fun updateLanguagePreference(languageCode: String): Result<String> {
        Log.d(TAG, "🔄 Updating language preference to '$languageCode' for notifications...")

        return try {
            if (!Language.isLanguageSupported(languageCode)) {
                Log.e(TAG, "❌ Language '$languageCode' is not supported")
                Log.d(TAG, "📋 Supported languages: ${Language.supportedLanguages}")
                return Result.Error(Exception("Language '$languageCode' is not supported"))
            }

            Log.d(TAG, "✅ Language '$languageCode' validated successfully")

            val request = UpdateLanguageRequest(languageCode)
            Log.d(TAG, "📡 Sending language update to server: $request")

            val response = api.updateLanguagePreference(request)

            if (response.success) {
                Log.d(TAG, "🎉 Server successfully updated language to: '${response.language}'")

                authPreferencesRepository.saveLanguagePreference(response.language)
                Log.d(TAG, "💾 Updated local cache with language: '${response.language}'")

                languageRefreshManager.notifyLanguageChanged(response.language)
                Log.d(
                    TAG,
                    "📢 Notified all app components of language change to: '${response.language}'"
                )
                Log.d(
                    TAG,
                    "🌍 Future API requests will include Accept-Language: ${response.language}"
                )
                Log.d(
                    TAG,
                    "🔔 Server will now send notifications in: ${
                        Language.getLanguageDisplayName(response.language)
                    }"
                )

                Result.Success(response.language)
            } else {
                Log.e(TAG, "❌ Server rejected language update: ${response.message}")
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Network error during language update: ${e.message}")
            Log.d(TAG, "🔄 Saving language locally for offline functionality")

            authPreferencesRepository.saveLanguagePreference(languageCode)
            Log.d(TAG, "💾 Saved '$languageCode' to local cache (offline)")

            languageRefreshManager.notifyLanguageChanged(languageCode)
            Log.d(TAG, "📢 Notified app components of offline language change")
            Log.w(TAG, "⏳ Language will sync with server when connection restored")

            Result.Error(e)
        }
    }

    override suspend fun getSupportedLanguages(): List<String> {
        Log.d(TAG, "📋 Getting supported languages: ${Language.supportedLanguages}")
        return Language.supportedLanguages
    }

    private fun getDeviceLanguageWithLogging(): String {
        val deviceLocale = Locale.getDefault()
        val deviceLanguage = deviceLocale.language
        val deviceCountry = deviceLocale.country

        Log.d(
            TAG,
            "🔍 Device locale detected: $deviceLanguage-$deviceCountry (${deviceLocale.displayName})"
        )

        return if (Language.supportedLanguages.contains(deviceLanguage)) {
            Log.d(TAG, "✅ Device language '$deviceLanguage' is supported for notifications")
            deviceLanguage
        } else {
            Log.w(
                TAG,
                "⚠️ Device language '$deviceLanguage' not supported, falling back to English"
            )
            Log.d(TAG, "📋 Supported languages: ${Language.supportedLanguages}")
            Language.ENGLISH
        }
    }
}