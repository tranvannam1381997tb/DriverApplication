package com.example.driverapplication.firebase

import com.example.driverapplication.manager.AccountManager
import org.json.JSONException
import org.json.JSONObject

class FirebaseUtils {
    companion object {
        fun validateInfoUser(jsonData: String?): Boolean {
            if (jsonData.isNullOrEmpty()) {
                return false
            }
            try {
                val jsonObject = JSONObject(jsonData)

                val driverId = jsonObject.getString(FirebaseConstants.KEY_DRIVER_ID)
                if (driverId.isNullOrEmpty() || driverId != AccountManager.getInstance().getDriverId()) {
                    return false
                }

                if (jsonObject.getString(FirebaseConstants.KEY_START_ADDRESS).isNullOrEmpty()) {
                    return false
                }
                if (jsonObject.getString(FirebaseConstants.KEY_END_ADDRESS).isNullOrEmpty()) {
                    return false
                }
                if (jsonObject.getString(FirebaseConstants.KEY_USER_ID).isNullOrEmpty()) {
                    return false
                }
                if (jsonObject.getString(FirebaseConstants.KEY_PRICE).isNullOrEmpty()) {
                    return false
                }
                if (jsonObject.getString(FirebaseConstants.KEY_DISTANCE).isNullOrEmpty()) {
                    return false
                }
                return true
            } catch (e: JSONException) {
                return false
            }
        }

        fun validateJsonCancelBook(jsonData: String?): Boolean {
            if (jsonData.isNullOrEmpty()) {
                return false
            }
            try {
                val jsonObject = JSONObject(jsonData)

                val driverId = jsonObject.getString(FirebaseConstants.KEY_DRIVER_ID)
                if (driverId.isNullOrEmpty() || driverId != AccountManager.getInstance().getDriverId()) {
                    return false
                }
                if (jsonObject.getString(FirebaseConstants.KEY_USER_ID).isNullOrEmpty()) {
                    return false
                }

                return true
            } catch (e: JSONException) {
                return false
            }
        }
    }
}