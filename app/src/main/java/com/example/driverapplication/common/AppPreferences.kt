package com.example.driverapplication.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.driverapplication.models.BookInfo
import com.google.gson.Gson

/**
 * Created by NamTV on 27/3/20.
 */
@SuppressLint("CommitPrefEdits")
class AppPreferences private constructor(context: Context) {
    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var editor: SharedPreferences.Editor = prefs.edit()

    var bookInfoPreferences: BookInfo? = null
        get() {
            val gson = Gson()
            val json = prefs.getString(BOOK_INFO, "")
            return gson.fromJson(json, BookInfo::class.java)
        }
        set(value) {
            field = value
            val gson = Gson()
            val json = gson.toJson(value)
            editor.putString(BOOK_INFO, json).commit()
        }

    companion object : SingletonHolder<AppPreferences, Context>(::AppPreferences) {
        const val BOOK_INFO = "bookInfo"
    }

}