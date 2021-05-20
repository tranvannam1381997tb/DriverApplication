package com.example.driverapplication.connection

import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.driverapplication.DriverApplication
import com.example.driverapplication.R
import com.example.driverapplication.common.AccountManager
import com.example.driverapplication.common.Constants
import com.example.driverapplication.firebase.FirebaseConstants
import com.example.driverapplication.model.DriverInfoKey
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class HttpConnection private constructor() {

    fun startLogin(jsonBody: JSONObject, callback:(Boolean, String) -> Unit) {
        val url = String.format(URL_LOGIN_FORMAT, HOST)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, Response.Listener<JSONObject> {
            callback.invoke(true, it.toString())
        }, Response.ErrorListener {
            try {
                if (it.networkResponse != null) {
                    val statusCode = it.networkResponse.statusCode
                    if (statusCode == 400) {
                        callback.invoke(false, DriverApplication.getAppContext().getString(R.string.no_account))
                        return@ErrorListener
                    }
                }
            } catch (e: Exception) {
                Log.d("NamTV", "HttpConnection::startLogin::exception")
            }

            callback.invoke(false, DriverApplication.getAppContext().getString(R.string.connect_server_error))
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Accept"] = "application/json"
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(jsonObjectRequest)
    }

    fun startSignUp(jsonBody: JSONObject, callback:(Boolean, String) -> Unit) {
        val url = String.format(URL_SIGN_UP, HOST)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, Response.Listener<JSONObject> {
            callback.invoke(true, it.toString())
        }, Response.ErrorListener {
            if (it.networkResponse != null) {
                val statusCode = it.networkResponse.statusCode
                if (statusCode == 400 && it.networkResponse.data != null) {
                    val dataError = JSONObject(it.networkResponse.data.toString(Charsets.UTF_8))
                    val error = dataError.getString("error")
                    callback.invoke(false, error)
                    return@ErrorListener
                }
            }

            callback.invoke(false, DriverApplication.getAppContext().getString(R.string.connect_server_error))
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Accept"] = "application/json"
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(jsonObjectRequest)
    }

    fun startArriving(jsonBody: JSONObject, callback:(Boolean, String) -> Unit) {
        val url = String.format(URL_START_ARRIVING, HOST)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, Response.Listener<JSONObject> {
            callback.invoke(true, it.toString())
        }, Response.ErrorListener {
            if (it.networkResponse != null && it.networkResponse.data.isEmpty()) {
                val statusCode = it.networkResponse.statusCode
                if (statusCode == 400) {
                    val dataError = JSONObject(it.networkResponse.data.toString(Charsets.UTF_8))
                    val error = dataError.getString("error")
                    callback.invoke(false, error)
                    return@ErrorListener
                }
            }

            callback.invoke(false, DriverApplication.getAppContext().getString(R.string.connect_server_error))
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Accept"] = "application/json"
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(jsonObjectRequest)
    }

    fun startGoing(jsonBody: JSONObject, callback:(Boolean, String) -> Unit) {
        val url = String.format(URL_START_GOING, HOST)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, Response.Listener<JSONObject> {
            callback.invoke(true, it.toString())
        }, Response.ErrorListener {
            if (it.networkResponse != null && it.networkResponse.data.isEmpty()) {
                val statusCode = it.networkResponse.statusCode
                if (statusCode == 400) {
                    val dataError = JSONObject(it.networkResponse.data.toString(Charsets.UTF_8))
                    val error = dataError.getString("error")
                    callback.invoke(false, error)
                    return@ErrorListener
                }
            }

            callback.invoke(false, DriverApplication.getAppContext().getString(R.string.connect_server_error))
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Accept"] = "application/json"
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(jsonObjectRequest)
    }

    fun updateStatusDriver() {
        val url = String.format(URL_UPDATE_STATUS_DRIVER, HOST)
        val jsonBody = JSONObject()
        jsonBody.put(DriverInfoKey.KeyDriverId.rawValue, AccountManager.getInstance().getDriverId())
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, Response.Listener<JSONObject> {
            Log.d("NamTV", "updateStatusDriver $it + $jsonBody")
        }, Response.ErrorListener {
            Log.d("NamTV", "updateStatusDriver ErrorListener $it")
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Accept"] = "application/json"
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            CONNECTION_TIMEOUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(jsonObjectRequest)
    }

    fun logout(callback:(Boolean) -> Unit) {
        val url = String.format(URL_LOGOUT, HOST)
        val jsonBody = JSONObject()
        jsonBody.put(DriverInfoKey.KeyDriverId.rawValue, AccountManager.getInstance().getDriverId())
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonBody, Response.Listener<JSONObject> {
            Log.d("NamTV", "updateStatusDriver $it + $jsonBody")
            callback.invoke(true)
        }, Response.ErrorListener {
            Log.d("NamTV", "updateStatusDriver ErrorListener $it")
            callback.invoke(false)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=utf-8"
                params["Accept"] = "application/json"
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            CONNECTION_TIMEOUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(jsonObjectRequest)
    }

    companion object {
        private const val URL_LOGIN_FORMAT = "http://%s/api/driver/login"
        private const val URL_SIGN_UP = "http://%s/api/driver/create"
        private const val URL_START_ARRIVING = "http://%s/api/driver/arriving"
        private const val URL_START_GOING = "http://%s/api/driver/going"
        private const val URL_UPDATE_STATUS_DRIVER = "http://%s/api/driver/update-status"
        private const val URL_LOGOUT = "http://%s/api/driver/logout"

        private const val HOST = "192.168.1.215:3000"
        private const val CONNECTION_TIMEOUT = 30000

        private var instance: HttpConnection? = null
        fun getInstance(): HttpConnection {
            if (instance == null) {
                synchronized(HttpConnection::class.java) {
                    if (instance == null) {
                        instance = HttpConnection()
                    }
                }
            }
            return instance!!
        }
    }
}