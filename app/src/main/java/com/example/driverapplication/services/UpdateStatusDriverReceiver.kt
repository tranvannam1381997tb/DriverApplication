package com.example.driverapplication.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class UpdateStatusDriverReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
//            DriverManager.getInstance().getListDriverFromServer()
            Log.d("NamTV", "UpdateStatusDriverReceiver::schedule update status driver")
        }
    }
}