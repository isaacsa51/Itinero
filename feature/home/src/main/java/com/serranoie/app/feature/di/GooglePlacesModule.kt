/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: GooglePlacesModule.kt
 - Project: Itinero
 - Module: Itinero.feature.home.main
 -
 - Last edited: 19 agosto 2025
 */

package com.serranoie.app.feature.di

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.koin.dsl.module

val googlePlacesModule = module {
    single<PlacesClient> {
        val context: Context = get()
        if (!Places.isInitialized()) {
            val ai: ApplicationInfo = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val apiKey = ai.metaData.getString("com.google.android.geo.API_KEY")
                ?: throw IllegalStateException("Google Places API key not found in manifest")
            Places.initialize(context, apiKey)
        }
        Places.createClient(context)
    }
}