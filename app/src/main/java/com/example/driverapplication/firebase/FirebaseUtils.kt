package com.example.driverapplication.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.example.driverapplication.manager.AccountManager
import com.example.driverapplication.common.Constants
import com.google.firebase.database.DataSnapshot
import org.json.JSONException
import org.json.JSONObject
import java.time.format.DateTimeFormatter

class FirebaseUtils {
    companion object {

        fun getDoubleFromDataSnapshot(snapshot: DataSnapshot, key: String): Double {
            return (snapshot.child(key).getValue(Double::class.java)) ?: (-1).toDouble()
        }

        fun getIntFromDataSnapshot(snapshot: DataSnapshot, key: String): Int {
            return (snapshot.child(key).getValue(Int::class.java)) ?: -1
        }

        fun getStringFromDataSnapshot(snapshot: DataSnapshot, key: String): String {
            return (snapshot.child(key).getValue(String()::class.java)) ?: ""
        }

        fun getDateFromDataSnapshot(snapshot: DataSnapshot, key: String): String {
            val dateString = (snapshot.child(key).getValue(String()::class.java)) ?: ""
            return convertStringToDateFirebase(dateString)
        }

//        fun getTypeDriverFromDataSnapshot(snapshot: DataSnapshot, key: String): String {
//            val typeDriver = snapshot.child(key).getValue(Int::class.java)
//            if (typeDriver == TYPE_GRAB_BIKE) {
//                return GrabApplication.getAppContext().getString(R.string.grab_bike)
//            }
//            if (typeDriver == TYPE_GRAB_CAR) {
//                return GrabApplication.getAppContext().getString(R.string.grab_car)
//            }
//            return ""
//        }

        @SuppressLint("SimpleDateFormat")
        fun convertStringToDateFirebase(dateString: String): String {
            try {
                val formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FOR_FIREBASE)
                val date = formatter.parse(dateString)

                return DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_APP).format(date)
            } catch (e: Exception) {
                Log.d("NamTV", "CommonUtils::convertStringToDate: exception = $e")
            }

            return ""
        }

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