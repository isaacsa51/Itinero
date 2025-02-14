package org.serranoie.feature.itinero.onboard

data class Page(
    val title: String,
    val description: String,
    //@DrawableRes val image: Int,
)

val pages = listOf(
    Page(
        title = "Welcome to Itinero",
        description = "Itinero is a travel app that helps you plan your next adventure."
    ),

    Page(
        title = "Discover new places",
        description = "With the help of your group and the location selected, between you and Itinero will help you discover and plan your next adventure."
    ),

    Page(
        title = "Plan your trip",
        description = "Itinero allows you to create a personalized itinerary that fits your travel preferences."
    )
)