package com.example.driverapplication.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.driverapplication.DriverApplication
import com.example.driverapplication.R
import com.example.driverapplication.activities.MainActivity
import com.example.driverapplication.common.Constants
import com.example.driverapplication.firebase.FirebaseConstants

import com.example.driverapplication.firebase.FirebaseUtils
import com.google.firebase.messaging.FirebaseMessagingService

class DriverFirebaseMessagingService : FirebaseMessagingService() {
    private val notificationChannelId = "Driver Application tét"
    override fun handleIntent(intent: Intent?) {
        val action = intent?.action
        if ("com.google.android.c2dm.intent.RECEIVE" == action || "com.google.firebase.messaging.RECEIVE_DIRECT_BOOT" == action) {
            Log.d("NamTV", "handleIntent")
            if (intent.hasExtra(FirebaseConstants.KEY_BOOK_DRIVER)) {
                val bundle = intent.getBundleExtra(FirebaseConstants.KEY_BOOK_DRIVER)
                if (FirebaseUtils.validateInfoUser(bundle)) {
                    showNotification(DriverApplication.getAppContext(), "Có người book chuyến xe.", bundle!!)
                }
            }
        } else {
            super.handleIntent(intent)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("NamTV", "New Device Token $token")
    }

    private fun showNotification(context: Context, message: String, bundle: Bundle) {
        val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    notificationChannelId,
                    "Driver Application",
                    NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Driver Application"
            mNotificationManager.createNotificationChannel(channel)
        }
        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.NOTIFICATION_CONTENT, bundle)
        }
        val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.icon_notify) // notification icon
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))// message for notification
                .setAutoCancel(true) // clear notification after click
                .setGroup("driver_group")
                .setGroupSummary(false)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentIntent(pendingIntent)

        val mBuilderSummary = NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.icon_notify)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setAutoCancel(true)
                .setGroup("driver_group")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)

        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
        mNotificationManager.notify(0, mBuilderSummary.build())
        Log.d("NamTV", "set notify")
    }
}