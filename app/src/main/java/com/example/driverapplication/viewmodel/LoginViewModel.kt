package com.example.driverapplication.viewmodel

import android.content.Context
import android.content.Intent
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.driverapplication.activities.SignUpActivity

class LoginViewModel(var context: Context): ViewModel() {

    var isEnableBtnLogin: ObservableField<Boolean> = ObservableField(false)

    fun validateDataLogin(username: String?, password: String?) {
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            isEnableBtnLogin.set(true)
            return
        }
        isEnableBtnLogin.set(false)
    }

    fun startSignUp() {
        val intent = Intent(context, SignUpActivity::class.java)
        context.startActivity(intent)
    }
}