package org.serranoie.feature.itinero.onboard

import itinero.feature.onboard.generated.resources.Res
import itinero.feature.onboard.generated.resources.onboard_plan
import itinero.feature.onboard.generated.resources.onboard_planning
import itinero.feature.onboard.generated.resources.onboard_welcome
import org.jetbrains.compose.resources.DrawableResource

data class Page(
    val title: String,
    val description: String,
    val image: DrawableResource,
)

val pages = listOf(
    Page(
        title = "Welcome to Itinero",
        description = "Itinero is a travel app that helps you plan your next adventure.",
        image = Res.drawable.onboard_welcome
    ),

    Page(
        title = "Discover new places",
        description = "With the help of your group and the location selected, between you and Itinero will help you discover and plan your next adventure.",
        image = Res.drawable.onboard_plan

    ),

    Page(
        title = "Plan your trip",
        description = "Itinero allows you to create a personalized itinerary that fits your travel preferences.",
        image = Res.drawable.onboard_planning
    )
)