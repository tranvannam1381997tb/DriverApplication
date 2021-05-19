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
import org.json.JSONObject

class DriverFirebaseMessagingService : FirebaseMessagingService() {
    override fun handleIntent(intent: Intent?) {
        val action = intent?.action
        if ("com.google.android.c2dm.intent.RECEIVE" == action || "com.google.firebase.messaging.RECEIVE_DIRECT_BOOT" == action) {
            Log.d("NamTV", "handleIntent")
            if (intent.hasExtra(FirebaseConstants.KEY_BOOK_DRIVER)) {
                val jsonData = intent.getStringExtra(FirebaseConstants.KEY_BOOK_DRIVER)!!
                if (FirebaseUtils.validateInfoUser(jsonData)) {
                    bookListener?.handleBookRequest(JSONObject(jsonData))
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

    }

    companion object {
        var bookListener: BookListener? = null
    }
}

interface BookListener {
    fun handleBookRequest(jsonData: JSONObject)
}