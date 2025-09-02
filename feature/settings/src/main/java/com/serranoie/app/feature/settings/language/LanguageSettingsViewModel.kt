/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LanguageSettingsViewModel.kt
 - Project: Itinero
 - Module: feature.settings.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 27 enero 2025
 */

package com.serranoie.app.feature.settings.language

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.GetUserLanguagePreferenceUseCase
import com.serranoie.itinero.core.domain.usecase.UpdateLanguagePreferenceUseCase
import com.serranoie.itinero.core.domain.util.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LanguageSettingsState(
    val currentLanguage: String = Language.ENGLISH,
    val availableLanguages: List<LanguageItem> = emptyList(),
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val error: String? = null
)

data class LanguageItem(
    val code: String,
    val displayName: String,
    val isSelected: Boolean = false
)

class LanguageSettingsViewModel(
    private val getUserLanguagePreferenceUseCase: GetUserLanguagePreferenceUseCase,
    private val updateLanguagePreferenceUseCase: UpdateLanguagePreferenceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageSettingsState())
    val state: StateFlow<LanguageSettingsState> = _state.asStateFlow()

    companion object {
        private const val TAG = "LanguageSettingsUI"
    }

    init {
        Log.d(TAG, "🎨 Language Settings UI initialized")
        loadCurrentLanguage()
        loadAvailableLanguages()
    }

    private fun loadCurrentLanguage() {
        Log.d(TAG, "🔍 Loading current language preference for UI display...")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when (val result = getUserLanguagePreferenceUseCase()) {
                is Result.Success -> {
                    val currentLang = result.data
                    Log.d(
                        TAG,
                        "✅ Current language loaded: '$currentLang' (${
                            Language.getLanguageDisplayName(currentLang)
                        })"
                    )
                    _state.value = _state.value.copy(
                        currentLanguage = currentLang,
                        isLoading = false,
                        error = null
                    )
                    updateSelectedLanguage(currentLang)
                }

                is Result.Error -> {
                    Log.e(TAG, "❌ Failed to load language preference: ${result.exception.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to load current language preference"
                    )
                }
            }
        }
    }

    private fun loadAvailableLanguages() {
        Log.d(TAG, "📋 Loading available languages for UI...")
        val languages = Language.supportedLanguages.map { languageCode ->
            LanguageItem(
                code = languageCode,
                displayName = Language.getLanguageDisplayName(languageCode),
                isSelected = languageCode == _state.value.currentLanguage
            )
        }

        _state.value = _state.value.copy(availableLanguages = languages)
        Log.d(TAG, "✅ Loaded ${languages.size} available languages for selection")
    }

    private fun updateSelectedLanguage(selectedLanguage: String) {
        Log.d(TAG, "🔄 Updating UI selection to: '$selectedLanguage'")
        val updatedLanguages = _state.value.availableLanguages.map { language ->
            language.copy(isSelected = language.code == selectedLanguage)
        }

        _state.value = _state.value.copy(
            currentLanguage = selectedLanguage,
            availableLanguages = updatedLanguages
        )
    }

    fun selectLanguage(languageCode: String) {
        if (_state.value.isUpdating || languageCode == _state.value.currentLanguage) {
            Log.d(TAG, "⏭️ Skipping language selection - already selected or updating")
            return
        }

        Log.d(
            TAG,
            "👆 User selected language: '$languageCode' (${
                Language.getLanguageDisplayName(languageCode)
            })"
        )

        viewModelScope.launch {
            _state.value = _state.value.copy(isUpdating = true, error = null)
            Log.d(TAG, "🔄 Starting language update process...")

            when (val result = updateLanguagePreferenceUseCase(languageCode)) {
                is Result.Success -> {
                    Log.d(TAG, "🎉 Language update successful!")
                    Log.d(
                        TAG,
                        "🔔 Notifications will now be sent in: ${
                            Language.getLanguageDisplayName(result.data)
                        }"
                    )
                    Log.d(TAG, "🌍 HTTP headers will include: Accept-Language: ${result.data}")

                    // Language update successful - UI will be updated via the repository's
                    // language refresh event, but we also update immediately for responsiveness
                    updateSelectedLanguage(result.data)
                    _state.value = _state.value.copy(
                        isUpdating = false,
                        error = null
                    )
                }

                is Result.Error -> {
                    Log.e(TAG, "❌ Language update failed: ${result.exception.message}")
                    _state.value = _state.value.copy(
                        isUpdating = false,
                        error = "Failed to update language preference: ${result.exception.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        Log.d(TAG, "🧹 Clearing UI error state")
        _state.value = _state.value.copy(error = null)
    }
}