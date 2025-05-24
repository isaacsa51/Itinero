package com.serranoie.itinero.core.data.local.persistence

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_preferences")

class AuthPreferences(private val context: Context) {

    private val AUTH_TOKEN = stringPreferencesKey("auth_token")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data
            .map { preferences ->
                preferences[AUTH_TOKEN]
            }.first()
    }
}