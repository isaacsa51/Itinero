package com.serranoie.app.feature.onboard

import androidx.annotation.DrawableRes
import com.serranoie.itinero.feature.onboard.R

data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int,
)

val pages = listOf(
    Page(
        title = "Organize your next group trip with ease",
        description = "Plan itineraries, dates, and accommodations with all members synchronized in one place.",
        image = R.drawable.onboard_organize
    ),
    Page(
        title = "Shared expenses, itinerary updates and more",
        description = "Assign tasks, create a baggage list and keep everyone in sync with all the necessary and important information.",
        image = R.drawable.onboard_schedule
    ),
    Page(
        title = "Smart summary of you itinerary",
        description = "Visualize every day of your trip with key events automatically generated from your plan.",
        image = R.drawable.onboard_summary
    ),
    Page(
        title = "Chat with integrated assistance",
        description = "Communicate with your group and get contextual help through artificial intelligence in every conversation.",
        image = R.drawable.onboard_assistance
    ),
    Page(
        title = "Useful notifications, not annoying ones.",
        description = "Get reminders only when they matter: check-ins, departures, transportation, and more.",
        image = R.drawable.onboard_notif
    )
)
