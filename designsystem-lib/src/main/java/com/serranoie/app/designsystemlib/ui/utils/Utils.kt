/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: Utils.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 26 junio 2025
 */

package com.serranoie.app.designsystemlib.ui.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.core.net.toUri

object Utils {
    /**
     * Open the web link in the browser.
     *
     * @param context The context
     * @param url The URL to open
     */
    fun openWebLink(context: Context, url: String) {
        val uri: Uri = url.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (exc: ActivityNotFoundException) {
            exc.printStackTrace()
        }
    }

    fun dateToString(date: Date?): String {
        if (date == null) {
            return "Invalid date"
        }
        val outputFormatter = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
        outputFormatter.timeZone = TimeZone.getDefault()
        return try {
            outputFormatter.format(date)
        } catch (e: Exception) {
            "Invalid date format"
        }
    }

    fun formatPrice(price: Double): String {
        return String.format(Locale.getDefault(), "%.2f", price)
    }
}