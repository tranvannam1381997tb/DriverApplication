package com.example.driverapplication.viewmodel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.driverapplication.model.BookInfo

class MainViewModel(var context: Context): ViewModel() {
    var isShowingLayoutBook = ObservableField(false)
    var isShowingLayoutBottom = ObservableField(false)

    var bookInfo: BookInfo? = null
}