package com.example.driverapplication.firebase

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.driverapplication.DriverApplication
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject

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

    fun pushNotifyAgreeBook(userTokenId: String, timeArrivedOrigin: Int, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(userTokenId)
        val notification = createBodyRequestAgreeBook(userTokenId, timeArrivedOrigin)
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
        Log.d("NamTV", "pushNotifyAgreeBook = $notification")
    }

    private fun createBodyRequestAgreeBook(userTokenId: String, timeArrivedOrigin: Int): JSONObject {
        val notification = JSONObject()
        val notificationBody = JSONObject()
        val notificationData = JSONObject()

        try {
            notificationData.put(FirebaseConstants.KEY_DRIVER_GOING_BOOK, true)
            notificationData.put(FirebaseConstants.KEY_TIME_ARRIVED_ORIGIN, timeArrivedOrigin)
            notificationBody.put(FirebaseConstants.KEY_DRIVER_RESPONSE, notificationData)
            notification.put(FirebaseConstants.KEY_TO, userTokenId)
            notification.put(FirebaseConstants.KEY_DATA, notificationBody)
        } catch (e: JSONException) {
            Log.e("NamTV", "FirebaseConnection::pushNotifyToDriver: $e")
        } finally {
            return notification
        }
    }

    fun pushNotifyRejectBook(userTokenId: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(userTokenId)
        val notification = createBodyRequestRejectBook(userTokenId)
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
        Log.d("NamTV", "pushNotifyRejectBook = $notification")
    }

    private fun createBodyRequestRejectBook(userTokenId: String): JSONObject {
        val notification = JSONObject()
        val notificationBody = JSONObject()
        val notificationData = JSONObject()

        try {
            notificationData.put(FirebaseConstants.KEY_DRIVER_REJECT, true)
            notificationBody.put(FirebaseConstants.KEY_DRIVER_RESPONSE, notificationData)
            notification.put(FirebaseConstants.KEY_TO, userTokenId)
            notification.put(FirebaseConstants.KEY_DATA, notificationBody)
        } catch (e: JSONException) {
            Log.e("NamTV", "FirebaseConnection::createBodyRequestRejectBook: $e")
        } finally {
            return notification
        }
    }

    fun pushNotifyArrivedOrigin(userTokenId: String, startAddress: String, timeArrivedDestination: Int, callback: (Boolean)
    -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(userTokenId)
        val notification = createBodyRequestArrivedOrigin(userTokenId, startAddress, timeArrivedDestination)
        Log.d("NamTV", "pushNotifyArrivedOrigin: $notification")
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
        Log.d("NamTV", "pushNotifyArrivedOrigin = $notification")
    }

    private fun createBodyRequestArrivedOrigin(userTokenId: String, startAddress: String, timeArrivedDestination: Int):
            JSONObject {
        val notification = JSONObject()
        val notificationBody = JSONObject()
        val notificationData = JSONObject()

        try {
            notificationData.put(FirebaseConstants.KEY_DRIVER_ARRIVED_ORIGIN, true)
            notificationData.put(FirebaseConstants.KEY_TIME_ARRIVED_DESTINATION, timeArrivedDestination)
            notificationData.put(FirebaseConstants.KEY_START_ADDRESS, startAddress)
            notificationBody.put(FirebaseConstants.KEY_DRIVER_RESPONSE, notificationData)
            notification.put(FirebaseConstants.KEY_TO, userTokenId)
            notification.put(FirebaseConstants.KEY_DATA, notificationBody)
        } catch (e: JSONException) {
            Log.e("NamTV", "FirebaseConnection::createBodyRequestArrived: $e")
        } finally {
            return notification
        }
    }

    fun pushNotifyArrivingDestination(userTokenId: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(userTokenId)
        val notification = createBodyRequestArrivingDestination(userTokenId)
        Log.d("NamTV", "pushNotifyGoing: $notification")
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
                Log.d("NamTV", "pushNotifyArrivingDestination Response.ErrorListener + $it")
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
        Log.d("NamTV", "pushNotifyArrivingDestination = $notification")
    }

    private fun createBodyRequestArrivingDestination(userTokenId: String): JSONObject {
        val notification = JSONObject()
        val notificationBody = JSONObject()
        val notificationData = JSONObject()

        try {
            notificationData.put(FirebaseConstants.KEY_DRIVER_GOING, true)
            notificationBody.put(FirebaseConstants.KEY_DRIVER_RESPONSE, notificationData)
            notification.put(FirebaseConstants.KEY_TO, userTokenId)
            notification.put(FirebaseConstants.KEY_DATA, notificationBody)
        } catch (e: JSONException) {
            Log.e("NamTV", "FirebaseConnection::createBodyRequestArrived: $e")
        } finally {
            return notification
        }
    }

    fun pushNotifyArrivedDestination(userTokenId: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(userTokenId)
        val notification = createBodyRequestArrivedDestination(userTokenId)
        Log.d("NamTV", "pushNotifyArrivedDestination: $notification")
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
        Log.d("NamTV", "pushNotifyArrivedDestination = $notification")
    }

    private fun createBodyRequestArrivedDestination(userTokenId: String): JSONObject {
        val notification = JSONObject()
        val notificationBody = JSONObject()
        val notificationData = JSONObject()

        try {
            notificationData.put(FirebaseConstants.KEY_DRIVER_ARRIVED_DESTINATION, true)
            notificationBody.put(FirebaseConstants.KEY_DRIVER_RESPONSE, notificationData)
            notification.put(FirebaseConstants.KEY_TO, userTokenId)
            notification.put(FirebaseConstants.KEY_DATA, notificationBody)
        } catch (e: JSONException) {
            Log.e("NamTV", "FirebaseConnection::createBodyRequestArrived: $e")
        } finally {
            return notification
        }
    }

    fun pushNotifyBill(userTokenId: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(userTokenId)
        val notification = createBodyRequestBill(userTokenId)
        Log.d("NamTV", "pushNotifyBill: $notification")
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
        Log.d("NamTV", "pushNotifyBill = $notification")
    }

    private fun createBodyRequestBill(userTokenId: String): JSONObject {
        val notification = JSONObject()
        val notificationBody = JSONObject()
        val notificationData = JSONObject()

        try {
            notificationData.put(FirebaseConstants.KEY_DRIVER_BILL, true)
            notificationBody.put(FirebaseConstants.KEY_DRIVER_RESPONSE, notificationData)
            notification.put(FirebaseConstants.KEY_TO, userTokenId)
            notification.put(FirebaseConstants.KEY_DATA, notificationBody)
        } catch (e: JSONException) {
            Log.e("NamTV", "FirebaseConnection::createBodyRequestArrived: $e")
        } finally {
            return notification
        }
    }
}