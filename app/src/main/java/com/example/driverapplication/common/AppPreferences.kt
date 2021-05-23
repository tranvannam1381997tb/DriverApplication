package com.example.driverapplication.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.driverapplication.firebase.FirebaseConstants
import com.example.driverapplication.model.BookInfo
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by NamTV on 27/3/20.
 */
@SuppressLint("CommitPrefEdits")
class AppPreferences private constructor(context: Context) {
    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var editor: SharedPreferences.Editor = prefs.edit()

    fun saveBookInfoToPreferences(bookInfo: BookInfo) {
        val dataBookInfo = JSONObject()
        dataBookInfo.put(FirebaseConstants.KEY_USER_ID, bookInfo.userId)
        dataBookInfo.put(FirebaseConstants.KEY_TOKEN_ID, bookInfo.tokenId)
        dataBookInfo.put(FirebaseConstants.KEY_NAME, bookInfo.name)
        dataBookInfo.put(FirebaseConstants.KEY_AGE, bookInfo.age)
        dataBookInfo.put(FirebaseConstants.KEY_SEX, bookInfo.sex)
        dataBookInfo.put(FirebaseConstants.KEY_PHONE_NUMBER, bookInfo.phoneNumber)
        dataBookInfo.put(FirebaseConstants.KEY_START_ADDRESS, bookInfo.startAddress)
        dataBookInfo.put(FirebaseConstants.KEY_END_ADDRESS, bookInfo.endAddress)
        dataBookInfo.put(FirebaseConstants.KEY_LAT_START, bookInfo.latStart)
        dataBookInfo.put(FirebaseConstants.KEY_LNG_START, bookInfo.lngStart)
        dataBookInfo.put(FirebaseConstants.KEY_LAT_END, bookInfo.latEnd)
        dataBookInfo.put(FirebaseConstants.KEY_LNG_END, bookInfo.lngEnd)
        dataBookInfo.put(FirebaseConstants.KEY_PRICE, bookInfo.price)
        dataBookInfo.put(FirebaseConstants.KEY_DISTANCE, bookInfo.distance)

        bookInfoPreferences = dataBookInfo
    }

    fun getBookInfoFromPreferences(): BookInfo? {
        if (bookInfoPreferences == null) {
            return null
        }
        return BookInfo(
            userId = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_USER_ID),
            tokenId = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_TOKEN_ID),
            name = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_NAME),
            age = CommonUtils.getIntFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_AGE),
            sex = CommonUtils.getSexValue(CommonUtils.getIntFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_SEX)),
            phoneNumber = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_PHONE_NUMBER),
            startAddress = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_START_ADDRESS),
            endAddress = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_END_ADDRESS),
            latStart = CommonUtils.getDoubleFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_LAT_START),
            lngStart = CommonUtils.getDoubleFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_LNG_START),
            latEnd = CommonUtils.getDoubleFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_LAT_END),
            lngEnd = CommonUtils.getDoubleFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_LNG_END),
            price = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_PRICE),
            distance = CommonUtils.getStringFromJsonObject(bookInfoPreferences!!, FirebaseConstants.KEY_DISTANCE)
        )
    }

    private var bookInfoPreferences: JSONObject? = null
        get() {
            return try {
                JSONObject(prefs.getString(BOOK_INFO, "")!!)
            } catch (e: JSONException) {
                null
            }
        }
        set(value) {
            field = value
            editor.putString(BOOK_INFO, field.toString()).commit()
        }

    companion object : SingletonHolder<AppPreferences, Context>(::AppPreferences) {
        const val BOOK_INFO = "bookInfo"
    }

}