package com.serranoie.itinero.core.domain.repository

interface NotificationRepository {

    suspend fun showPendingMembersNotification(pendingCount: Int, tripNames: List<String>)

    suspend fun showExpenseNotification(
        expenseName: String, owedAmount: Double, creditorName: String, tripName: String
    )

    suspend fun showExpenseSettlementNotification(
        settledAmount: Double, debtorName: String, tripName: String
    )

    suspend fun showGenericNotification(
        title: String, body: String, data: Map<String, String> = emptyMap()
    )

    fun cancelPendingMembersNotification()

    suspend fun showTestNotification()

    suspend fun hasNotificationPermission(): Boolean
}