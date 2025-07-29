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
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    fun formatCurrency(price: Double): String {
        val format = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
        return try {
            format.format(price)
        } catch (e: Exception) {
            "$ $price" // Fallback to simple dollar formatting
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

    fun dateToFormattedString(date: Date?): String {
        if (date == null) return ""
        val targetFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
        return targetFormat.format(date)
    }

    fun View.toggleFeedback() {
        this.performHapticFeedback(HapticFeedbackConstants.TOGGLE_ON)
    }

    fun View.weakHapticFeedback() {
        this.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    fun View.strongHapticFeedback() {
        this.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    fun View.confirmFeedback() {
        this.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    fun View.errorFeedback() {
        try {
            val vibrator = this.context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                // Custom haptic pattern with increasing intensity (extracted from CustomHapticView)
                val numberOfPulses = 2 // Number of increasing haptic pulses
                val pulseDuration = 75L // Duration of each pulse in milliseconds
                val spaceBetweenPulses = 24L // Duration of space between pulses in milliseconds
                val maxAmplitude = 255 // Maximum amplitude for the last pulse

                val timings = LongArray(numberOfPulses * 2) // Double the size for on/off
                val amplitudes = IntArray(numberOfPulses * 2)

                for (i in 0 until numberOfPulses) {
                    val amplitude =
                        (maxAmplitude * (i + 1) / numberOfPulses) // Calculate increasing amplitude
                    timings[i * 2] = spaceBetweenPulses // Space before the pulse
                    timings[i * 2 + 1] = pulseDuration // Duration of the pulse
                    amplitudes[i * 2] = 0 // Amplitude of the space
                    amplitudes[i * 2 + 1] = amplitude // Amplitude of the pulse
                }

                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                it.vibrate(effect)
            }
        } catch (e: Exception) {
            // Fallback to basic haptic feedback if custom vibration fails
            this.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    fun String.toToast(context: Context, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, this, length).show()
    }
}