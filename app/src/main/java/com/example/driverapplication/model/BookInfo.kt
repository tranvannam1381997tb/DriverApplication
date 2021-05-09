package com.example.driverapplication.model

import android.os.Parcelable
import com.example.driverapplication.common.SexValue
import kotlinx.android.parcel.Parcelize
import kotlin.properties.Delegates

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


