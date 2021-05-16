package com.example.driverapplication.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import com.example.driverapplication.DriverApplication
import com.example.driverapplication.model.SexValue
import com.google.firebase.database.DataSnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.util.*


class CommonUtils {
    companion object {

        fun getJsonArrayFromJsonObject(jsonObject: JSONObject, key: String): JSONArray {
            return if (jsonObject.has(key)) {
                jsonObject.getJSONArray(key)
            } else {
                JSONArray()
            }
        }

        fun getJsonObjectFromJsonObject(jsonObject: JSONObject, key: String): JSONObject {
            return if (jsonObject.has(key)) {
                jsonObject.getJSONObject(key)
            } else {
                JSONObject()
            }
        }

        fun getStringFromJsonObject(jsonObject: JSONObject, key: String): String {
            return if (jsonObject.has(key)) {
                jsonObject.getString(key)
            } else {
                ""
            }
        }

        fun getDoubleFromJsonObject(jsonObject: JSONObject, key: String): Double {
            var data: Double? = null
            if (jsonObject.has(key)) {
                data = jsonObject.getString(key).toDoubleOrNull()
            }
            if (data == null) {

                data = 0.0
            }
            return data
        }

        fun getIntFromJsonObject(jsonObject: JSONObject, key: String) : Int {
            return if (jsonObject.has(key)) {
                jsonObject.getInt(key)
            } else {
                0
            }
        }

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

        fun clearFocusEditText(activity: Activity) {
            val view = activity.currentFocus
            if (view != null && view is EditText) {
                hideKeyboard(activity)
                // Resign focus EditText
                view.isFocusable = false
                view.isFocusable = true
                view.isFocusableInTouchMode = true
            }
        }

        private fun hideKeyboard(activity: Activity){
            val inputMethod: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethod.hideSoftInputFromWindow(activity.currentFocus!!.windowToken!!, 0)
        }

        fun getSexValue(sex: Int): String {
            return if (sex == 0) {
                SexValue.MALE.rawValue
            } else {
                SexValue.FEMALE.rawValue
            }
        }

        fun vibrateDevice() {
            val vibrator = DriverApplication.getAppContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}