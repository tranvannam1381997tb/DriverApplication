package com.example.driverapplication.viewmodel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class MainViewModel(var context: Context): ViewModel() {
    var isShowMapLayout = ObservableField(true)
}