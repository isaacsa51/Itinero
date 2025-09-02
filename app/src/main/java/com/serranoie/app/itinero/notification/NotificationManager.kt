package com.serranoie.app.itinero.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.serranoie.app.itinero.R
import com.serranoie.app.itinero.ui.MainActivity

class ItineroNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "pending_members_channel"
        private const val CHANNEL_NAME = "Pending Members"
        private const val CHANNEL_DESCRIPTION = "Notifications for pending group member requests"
        private const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
            setShowBadge(true)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showPendingMembersNotification(pendingCount: Int, tripNames: List<String>) {
        if (!hasNotificationPermission()) {
            Log.d(
                "ItineroNotificationManager",
                "Notification permission not granted. Not showing notification."
            )
            return
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

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "show_pending_members")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            Log.d(
                "ItineroNotificationManager",
                "Notification shown: title='$title', content='$content'"
            )
        } catch (e: SecurityException) {
            Log.e(
                "ItineroNotificationManager",
                "SecurityException when showing notification: ${e.message}"
            )
            // Handle case where notification permission is revoked at runtime
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
            Log.d("ItineroNotificationManager", "Notification enabled(Tiramisu+): $enabled")
            enabled
        } else {
            Log.d("ItineroNotificationManager", "Notification assumed enabled (below Tiramisu).")
            true
        }
    }

    fun cancelPendingMembersNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    /**
     * Test method to show a sample notification - useful for debugging
     */
    fun showTestNotification() {
        Log.d("ItineroNotificationManager", "Showing test notification")
        showPendingMembersNotification(1, listOf("Test Trip"))
    }
}