package com.serranoie.itinero.core.data.local.persistence

import android.content.Context
import androidx.core.content.edit

class AuthPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString("token", token) }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun clear() {
        prefs.edit { clear() }
    }
}