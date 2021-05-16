package com.example.driverapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookInfo(
    var userId: String,
    var tokenId: String,
    var name: String,
    var age: Int,
    var sex: String,
    var phoneNumber: String,
    var startAddress: String,
    var endAddress: String,
    var price: String,
    var distance: String

): Parcelable


