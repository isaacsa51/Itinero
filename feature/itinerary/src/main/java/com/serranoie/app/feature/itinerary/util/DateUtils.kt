package com.serranoie.app.feature.itinerary.util

import java.time.LocalDate

fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
    return generateSequence(start) { it.plusDays(1) }.takeWhile { it <= end }.toList()
}