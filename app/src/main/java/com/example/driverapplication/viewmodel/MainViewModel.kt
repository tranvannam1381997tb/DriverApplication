package com.example.driverapplication.viewmodel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.driverapplication.model.BookInfo

class MainViewModel(var context: Context): ViewModel() {
    var isShowMapLayout = ObservableField(true)

    var bookInfo: BookInfo? = null
}