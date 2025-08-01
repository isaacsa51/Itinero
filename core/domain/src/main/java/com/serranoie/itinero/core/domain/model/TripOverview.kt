/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripOverview.kt
 - Project: Itinero
 - Module: Itinero.core.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 30 julio 2025
 */

package com.serranoie.itinero.core.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripOverview(
    @SerialName("date")
    val date: String,
    @SerialName("todayItinerary")
    val todayItinerary: List<TodayItinerary>,
    @SerialName("yesterdayDate")
    val yesterdayDate: String,
    @SerialName("yesterdayExpenses")
    val yesterdayExpenses: List<YesterdayExpense>
)

@Serializable
data class TodayItinerary(
    @SerialName("date")
    val date: String,
    @SerialName("description")
    val description: String,
    @SerialName("groupCode")
    val groupCode: String,
    @SerialName("id")
    val id: Int,
    @SerialName("isCompleted")
    val isCompleted: Boolean,
    @SerialName("location")
    val location: String,
    @SerialName("name")
    val name: String,
    @SerialName("time")
    val time: String
)

@Serializable
data class YesterdayExpense(
    @SerialName("amount")
    val amount: Double,
    @SerialName("category")
    val category: String,
    @SerialName("date")
    val date: String,
    @SerialName("debtors")
    val debtors: List<Debtor>,
    @SerialName("id")
    val id: Int,
    @SerialName("isCompleted")
    val isCompleted: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("notes")
    val notes: String?,
    @SerialName("paidBy")
    val paidBy: PaidBy,
    @SerialName("paidByUserId")
    val paidByUserId: Int,
    @SerialName("paymentMethod")
    val paymentMethod: String,
    @SerialName("splitType")
    val splitType: String,
    @SerialName("tripId")
    val tripId: Int
)

@Serializable
data class Debtor(
    @SerialName("amount")
    val amount: Double,
    @SerialName("hasPaid")
    val hasPaid: Boolean,
    @SerialName("id")
    val id: Int,
    @SerialName("splitValue")
    val splitValue: Double,
    @SerialName("user")
    val user: User,
    @SerialName("userId")
    val userId: Int
)

@Serializable
data class User(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("surname")
    val surname: String
)

@Serializable
data class PaidBy(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("surname")
    val surname: String
)