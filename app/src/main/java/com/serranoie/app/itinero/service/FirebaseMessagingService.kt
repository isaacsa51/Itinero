// app/src/main/java/com/serranoie/app/itinero/service/ItineroFirebaseMessagingService.kt
package com.serranoie.app.itinero.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.serranoie.itinero.core.domain.model.DeviceType
import com.serranoie.itinero.core.domain.repository.FCMRepository
import com.serranoie.itinero.core.domain.repository.NotificationRepository
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import android.provider.Settings

class ItineroFirebaseMessagingService : FirebaseMessagingService() {
    
    private val fcmRepository: FCMRepository by inject()
    private val notificationRepository: NotificationRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val TAG = "ItineroFCMService"
        private const val FALLBACK_CHANNEL_ID = "fcm_fallback_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createFallbackNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val data = remoteMessage.data
        val notification = remoteMessage.notification
        
        serviceScope.launch {
            try {
                val notificationType = data["type"]
                Log.d(TAG, "Processing notification type: $notificationType")

                when (notificationType) {
                    "expense" -> {
                        handleExpenseNotification(data, notification)
                    }
                    "payment" -> {
                        handlePaymentNotification(data, notification)
                    }

                    "expense_completed" -> {
                        handleExpenseCompletedNotification(data, notification)
                    }

                    "member_request" -> {
                        handleMemberRequestNotification(data, notification)
                    }

                    "trip_update" -> {
                        handleTripUpdateNotification(data, notification)
                    }

                    else -> {
                        Log.d(TAG, "Unknown or missing notification type, using default handling")
                        notification?.let {
                            showGenericNotification(
                                it.title ?: "Itinero Update",
                                it.body ?: "",
                                data
                            )
                        } ?: run {
                            Log.w(TAG, "No notification payload found and no type specified")
                            showGenericNotification("Itinero Update", "You have a new update", data)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing FCM message", e)
            }
        }
    }

    private fun createFallbackNotificationChannel() {
        val channel = NotificationChannel(
            FALLBACK_CHANNEL_ID,
            "Itinero Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Fallback channel for Itinero notifications"
            setShowBadge(true)
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "Fallback notification channel created")
    }

    private suspend fun handleExpenseNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        val expenseName = data["expenseName"] ?: "Unknown Expense"
        val userAmount = data["userAmount"]?.toDoubleOrNull() ?: 0.0
        val payerName = data["payerName"] ?: "Someone"
        val tripId = data["tripId"] ?: ""

        // Use the server's title/body if available, otherwise create our own
        val title = notification?.title ?: "New Expense Added"
        val body =
            notification?.body ?: "You owe $${String.format("%.2f", userAmount)} for $expenseName"

        // For now, use existing method - you might want to create a generic one
        notificationRepository.showExpenseNotification(
            expenseName = expenseName,
            owedAmount = userAmount,
            creditorName = payerName,
            tripName = "Trip" // You might want to get trip name from another API call
        )
    }

    private suspend fun handlePaymentNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        val amount = data["amount"]?.toDoubleOrNull() ?: 0.0
        val debtorName = data["debtorName"] ?: "Someone"
        val expenseName = data["expenseName"] ?: "Unknown Expense"

        Log.d(TAG, "Payment received: $amount from $debtorName for $expenseName")

        val title = notification?.title ?: "Payment Received"
        val body = notification?.body ?: "$debtorName paid you $${String.format("%.2f", amount)}"

        notificationRepository.showExpenseSettlementNotification(
            settledAmount = amount,
            debtorName = debtorName,
            tripName = "Trip" // You might want to get trip name from another API call
        )
    }

    private suspend fun handleExpenseCompletedNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        val expenseName = data["expenseName"] ?: "Unknown Expense"
        val payerName = data["payerName"] ?: "Someone"

        Log.d(TAG, "Expense completed: $expenseName by $payerName")

        val title = notification?.title ?: "Expense Completed"
        val body = notification?.body ?: "$payerName marked \"$expenseName\" as completed"

        showGenericNotification(title, body, data)
    }

    private suspend fun handleMemberRequestNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        val tripName = data["trip_name"] ?: data["tripName"] ?: "Unknown Trip"
        val memberCount = data["member_count"]?.toIntOrNull() ?: 1
        
        Log.d(TAG, "Member request for trip: $tripName")
        
        notificationRepository.showPendingMembersNotification(
            pendingCount = memberCount,
            tripNames = listOf(tripName)
        )
    }

    private suspend fun handleTripUpdateNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        val tripName = data["trip_name"] ?: data["tripName"] ?: "Unknown Trip"
        val updateType = data["update_type"] ?: "updated"
        
        Log.d(TAG, "Trip update: $tripName - $updateType")

        val title = notification?.title ?: "Trip Updated"
        val body = notification?.body ?: "Your trip $tripName has been updated"

        showGenericNotification(title, body, data)
    }

    private suspend fun showGenericNotification(
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        try {
            notificationRepository.showGenericNotification(title, body, data)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing generic notification", e)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        serviceScope.launch {
            try {
                val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                
                when (val result = fcmRepository.saveFCMToken(
                    fcmToken = token,
                    deviceId = deviceId,
                    deviceType = DeviceType.ANDROID
                )) {
                    is Result.Success -> { }
                    is Result.Error -> {
                        Log.e(TAG, "Failed to register new FCM token: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error registering new FCM token", e)
            }
        }
    }
}