package com.serranoie.itinero.core.data.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.serranoie.itinero.core.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl(
    private val context: Context,
    private val mainActivityClassName: String = "com.serranoie.app.itinero.ui.MainActivity"
) : NotificationRepository {

    companion object {
        private const val CHANNEL_ID = "pending_members_channel"
        private const val EXPENSE_CHANNEL_ID = "expense_notifications_channel"
        private const val CHANNEL_NAME = "Pending Members"
        private const val EXPENSE_CHANNEL_NAME = "Expense Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for pending group member requests"
        private const val EXPENSE_CHANNEL_DESCRIPTION =
            "Notifications for expense updates and settlements"
        private const val NOTIFICATION_ID = 1001
        private const val EXPENSE_NOTIFICATION_ID = 1002
        private const val TAG = "NotificationRepository"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // Members channel
        val membersChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
            setShowBadge(true)
        }

        // Expense channel
        val expenseChannel = NotificationChannel(
            EXPENSE_CHANNEL_ID,
            EXPENSE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH // Higher priority for money matters
        ).apply {
            description = EXPENSE_CHANNEL_DESCRIPTION
            setShowBadge(true)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(membersChannel)
        notificationManager.createNotificationChannel(expenseChannel)
    }

    @SuppressLint("MissingPermission")
    override suspend fun showPendingMembersNotification(
        pendingCount: Int,
        tripNames: List<String>
    ) = withContext(Dispatchers.Main) {
        if (!hasNotificationPermission()) {
            Log.d(TAG, "Notification permission not granted. Not showing notification.")
            return@withContext
        }

        val title = if (pendingCount == 1) {
            "New member wants to join"
        } else {
            "$pendingCount members want to join"
        }

        val content = if (tripNames.size == 1) {
            "Someone wants to join \"${tripNames.first()}\""
        } else {
            "Members want to join your trips"
        }

        val intent = try {
            val mainActivityClass = Class.forName(mainActivityClassName)
            Intent(context, mainActivityClass).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "show_pending_members")
            }
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Main activity class not found: $mainActivityClassName", e)
            return@withContext
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            //NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            Log.d(TAG, "Notification shown: title='$title', content='$content'")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when showing notification: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun showExpenseNotification(
        expenseName: String,
        owedAmount: Double,
        creditorName: String,
        tripName: String
    ) = withContext(Dispatchers.Main) {
        if (!hasNotificationPermission()) {
            Log.d(TAG, "Notification permission not granted. Not showing expense notification.")
            return@withContext
        }

        val formattedAmount = String.format("%.2f", owedAmount)
        val title = "New Expense: $expenseName"
        val content = "You owe $$formattedAmount to $creditorName in $tripName"

        val intent = try {
            val mainActivityClass = Class.forName(mainActivityClassName)
            Intent(context, mainActivityClass).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "show_expense_details")
                putExtra("expense_name", expenseName)
                putExtra("trip_name", tripName)
                putExtra("creditor_name", creditorName)
                putExtra("amount", formattedAmount)
            }
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Main activity class not found: $mainActivityClassName", e)
            return@withContext
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(), // Unique ID for each notification
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, EXPENSE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Higher priority for money matters
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(), // Unique ID for each expense notification
                notification
            )
            Log.d(TAG, "Expense notification shown: $title")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when showing expense notification: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun showExpenseSettlementNotification(
        settledAmount: Double,
        debtorName: String,
        tripName: String
    ) = withContext(Dispatchers.Main) {
        if (!hasNotificationPermission()) return@withContext

        val formattedAmount = String.format("%.2f", settledAmount)
        val title = "Payment Received"
        val content = "$debtorName paid you $$formattedAmount in $tripName"

        val intent = try {
            val mainActivityClass = Class.forName(mainActivityClassName)
            Intent(context, mainActivityClass).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "show_expense_settlement")
                putExtra("debtor_name", debtorName)
                putExtra("trip_name", tripName)
                putExtra("amount", formattedAmount)
            }
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Main activity class not found: $mainActivityClassName", e)
            return@withContext
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, EXPENSE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
            Log.d(TAG, "Settlement notification shown: $title")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when showing settlement notification: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun showGenericNotification(
        title: String,
        body: String,
        data: Map<String, String>
    ) = withContext(Dispatchers.Main) {
        if (!hasNotificationPermission()) {
            Log.d(TAG, "Notification permission not granted. Not showing generic notification.")
            return@withContext
        }

        val intent = try {
            val mainActivityClass = Class.forName(mainActivityClassName)
            Intent(context, mainActivityClass).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                // Add all data as extras for deep linking
                data.forEach { (key, value) ->
                    putExtra(key, value)
                }

                // Determine action based on notification type
                val action = when (data["type"]) {
                    "expense", "payment", "expense_completed" -> "show_expense_details"
                    "trip_update" -> "show_trip_details"
                    "member_request" -> "show_pending_members"
                    else -> "show_generic_notification"
                }
                putExtra("action", action)
            }
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Main activity class not found: $mainActivityClassName", e)
            return@withContext
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(), // Unique ID for each notification
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, EXPENSE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(), // Unique ID for each notification
                notification
            )
            Log.d(TAG, "Generic notification shown: $title")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when showing generic notification: ${e.message}")
        }
    }

    override suspend fun hasNotificationPermission(): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
            Log.d(TAG, "Notification enabled(Tiramisu+): $enabled")
            enabled
        } else {
            Log.d(TAG, "Notification assumed enabled (below Tiramisu).")
            true
        }
    }

    override fun cancelPendingMembersNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        Log.d(TAG, "Pending members notification cancelled")
    }

    override suspend fun showTestNotification() {
        Log.d(TAG, "Showing test notification")
        showPendingMembersNotification(1, listOf("Test Trip"))
    }
}