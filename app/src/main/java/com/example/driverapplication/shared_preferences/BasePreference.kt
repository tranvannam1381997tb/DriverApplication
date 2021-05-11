package com.example.driverapplication.shared_preferences

import android.content.SharedPreferences

abstract class BasePreference {

    var prefs: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
}