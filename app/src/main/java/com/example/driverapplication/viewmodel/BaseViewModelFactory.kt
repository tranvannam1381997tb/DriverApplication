package com.example.driverapplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BaseViewModelFactory(private var context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> return LoginViewModel(
                context
            ) as T

            modelClass.isAssignableFrom(MainViewModel::class.java) -> return MainViewModel(
                context
            ) as T
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> return SignUpViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}