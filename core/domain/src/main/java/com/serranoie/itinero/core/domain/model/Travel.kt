package com.serranoie.itinero.core.domain.model

data class Travel(
    val id: String,
    val groupCode: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    // val totalDays: Int,
    val summary: String,
    val accommodation: String,
    val reservationCode: String,
    val extraInfo: String,
    val additionalInfo: String,
    val isOwner: Boolean
)
