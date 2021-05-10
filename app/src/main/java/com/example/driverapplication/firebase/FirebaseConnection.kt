package com.example.driverapplication.firebase

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.driverapplication.DriverApplication
import com.example.driverapplication.common.AccountManager
import com.example.driverapplication.common.Constants
import com.example.grabapplication.googlemaps.models.Distance
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject
import javax.security.auth.callback.Callback

class FirebaseConnection private constructor() {

    companion object {
        private var instance: FirebaseConnection? = null
        fun getInstance(): FirebaseConnection {
            if (instance == null) {
                synchronized(FirebaseConnection::class.java) {
                    if (instance == null) {
                        instance = FirebaseConnection()
                    }
                }
            }
            return instance!!
        }
    }

    fun pushNotifyAgreeBook(userTokenId: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(userTokenId)
        val notification = createBodyRequestPush(userTokenId)
        val jsonObjectRequest = object : JsonObjectRequest(FirebaseConstants.FCM_API, notification,
            Response.Listener<JSONObject> {
                Log.d("NamTV", "JsonObjectRequest Response.Listener + $it")
                if (it.has(FirebaseConstants.KEY_SUCCESS) && it.getInt(FirebaseConstants.KEY_SUCCESS) == 1) {
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }, Response.ErrorListener {
                callback.invoke(false)
                Log.d("NamTV", "JsonObjectRequest Response.ErrorListener + $it")
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params[FirebaseConstants.KEY_AUTHORIZATION] = FirebaseConstants.SERVER_KEY
                params[FirebaseConstants.KEY_CONTENT_TYPE] = FirebaseConstants.CONTENT_TYPE
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(jsonObjectRequest)
    }

    private fun createBodyRequestPush(userTokenId: String): JSONObject {
        val notification = JSONObject()
        val notificationBody = JSONObject()

        try {
            notificationBody.put(FirebaseConstants.KEY_DRIVER_GOING, true)
            notification.put(FirebaseConstants.KEY_TO, userTokenId)
            notification.put(FirebaseConstants.KEY_DATA, notificationBody)
        } catch (e: JSONException) {
            Log.e("NamTV", "FirebaseConnection::pushNotifyToDriver: $e")
        } finally {
            return notification
        }
    }
}