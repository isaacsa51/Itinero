/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripOverviewDto.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 30 julio 2025
 */

package com.serranoie.itinero.core.data.remote.dto.trip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripOverviewDto(
    @SerialName("date")
    val date: String? = null,
    @SerialName("todayItinerary")
    val todayItinerary: List<TodayItineraryDto> = emptyList(),
    @SerialName("yesterdayDate")
    val yesterdayDate: String? = null,
    @SerialName("yesterdayExpenses")
    val yesterdayExpenses: List<YesterdayExpenseDto> = emptyList()
)

@Serializable
data class TodayItineraryDto(
    @SerialName("date")
    val date: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("groupCode")
    val groupCode: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("isCompleted")
    val isCompleted: Boolean = false,
    @SerialName("location")
    val location: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("time")
    val time: String? = null
)

@Serializable
data class YesterdayExpenseDto(
    @SerialName("amount")
    val amount: Double = 0.0,
    @SerialName("category")
    val category: String? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("debtors")
    val debtors: List<DebtorDto> = emptyList(),
    @SerialName("id")
    val id: Int? = null,
    @SerialName("isCompleted")
    val isCompleted: Boolean? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("notes")
    val notes: String? = null,
    @SerialName("paidBy")
    val paidBy: PaidByDto? = null,
    @SerialName("paidByUserId")
    val paidByUserId: Int? = null,
    @SerialName("paymentMethod")
    val paymentMethod: String? = null,
    @SerialName("splitType")
    val splitType: String? = null,
    @SerialName("tripId")
    val tripId: Int? = null
)

@Serializable
data class DebtorDto(
    @SerialName("amount")
    val amount: Double = 0.0,
    @SerialName("hasPaid")
    val hasPaid: Boolean = false,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("splitValue")
    val splitValue: Double = 0.0,
    @SerialName("user")
    val user: UserDto? = null,
    @SerialName("userId")
    val userId: Int? = null
)

@Serializable
data class UserDto(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("surname")
    val surname: String? = null
)

@Serializable
data class PaidByDto(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("surname")
    val surname: String? = null
)
