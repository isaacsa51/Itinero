package com.serranoie.app.feature

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateFormattingTest {

    @Test
    fun `date formatting with UTC timezone works correctly`() {
        // Given
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("UTC")
            set(2025, Calendar.AUGUST, 20, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // When
        val formattedDate = formatter.format(Date(calendar.timeInMillis))

        // Then
        assertEquals("2025-08-20", formattedDate)
    }

    @Test
    fun `pretty date formatting works correctly`() {
        // Given
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("d 'of' MMMM", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val dateString = "2025-08-20"

        // When
        val date = inputFormat.parse(dateString)!!
        val prettyDate = outputFormat.format(date)

        // Then
        assertEquals("20 of August", prettyDate)
    }

    @Test
    fun `date range formatting works correctly`() {
        // Given
        val startDate = "2025-08-20"
        val endDate = "2025-08-25"

        fun getPrettyDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = inputFormat.parse(dateString)
                val outputFormat = SimpleDateFormat("d 'of' MMMM", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }

        // When
        val dateRange = "${getPrettyDate(startDate)} to ${getPrettyDate(endDate)}"

        // Then
        assertEquals("20 of August to 25 of August", dateRange)
    }

    @Test
    fun `date parsing handles invalid dates gracefully`() {
        // Given
        fun getPrettyDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = inputFormat.parse(dateString)
                val outputFormat = SimpleDateFormat("d 'of' MMMM", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString // Return original string on error
            }
        }

        // When
        val invalidDate = getPrettyDate("invalid-date")
        val emptyDate = getPrettyDate("")
        val wrongFormat = getPrettyDate("20/08/2025")

        // Then
        assertEquals("invalid-date", invalidDate)
        assertEquals("", emptyDate)
        assertEquals("20/08/2025", wrongFormat)
    }

    @Test
    fun `timezone consistency prevents date shifting`() {
        // Given
        val datePickerTimestamp = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("UTC")
            set(2025, Calendar.AUGUST, 20, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // When - format with UTC (correct approach)
        val utcFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val utcFormatted = utcFormatter.format(Date(datePickerTimestamp))

        // When - format with local timezone (could cause issues)
        val localFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val localFormatted = localFormatter.format(Date(datePickerTimestamp))

        // Then - UTC should be consistent
        assertEquals("2025-08-20", utcFormatted)
        // Local formatting might differ based on timezone, but we test the principle
        assertTrue("Date should contain year 2025", localFormatted.contains("2025"))
    }

    @Test
    fun `date validation logic for form fields`() {
        // Test the logic used in form validation
        fun isValidDateRange(startDate: String, endDate: String): Boolean {
            return startDate.isNotBlank() && endDate.isNotBlank()
        }

        // Test cases
        assertTrue("Valid dates should pass", isValidDateRange("2025-08-20", "2025-08-25"))
        assertTrue("Same dates should be allowed", isValidDateRange("2025-08-20", "2025-08-20"))
        assertEquals(false, isValidDateRange("", "2025-08-25"))
        assertEquals(false, isValidDateRange("2025-08-20", ""))
        assertEquals(false, isValidDateRange("", ""))
    }
}