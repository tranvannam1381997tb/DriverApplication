package com.example.driverapplication.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DriverInfo(
    var driverId: String,
    var tokenId: String,
    var name: String,
    var age: Int,
    var sex: Int,
    var phoneNumber: String,
    var latitude: Double,
    var longitude: Double,
    var rate: Double,
    var status: Int,
    var startDate: String,
    var typeDriver: String,
    var typeVehicle: String,
    var licensePlateNumber: String
) : Parcelable

enum class DriverInfoKey(val rawValue: String) {
    KeyDriverId("driverId"),
    KeyTokenId("tokenId"),
    KeyName("name"),
    KeyAge("age"),
    KeySex("sex"),
    KeyPhoneNumber("phoneNumber"),
    KeyPassword("password"),
    KeyLatitude("latitude"),
    KeyLongitude("longitude"),
    KeyRate("rate"),
    KeyStatus("status"),
    KeyStartDate("startDate"),
    KeyTypeDriver("typeDriver"),
    KeyTypeVehicle("typeVehicle"),
    KeyLicensePlateNumber("licensePlateNumber")
}

enum class DriverStatus(val rawValue: Int) {
    StatusOff(-1),
    StatusOn(0),
    StatusArrivingOrigin(1),
    StatusArrivedOrigin(2),
    StatusArrivingDestination(3),
    StatusArrivedDestination(4),
    StatusBilling(5)
}


enum class SexValue(val rawValue: String) {
    MALE("Nam"),
    FEMALE("Nữ")
}

enum class TypeDriverValue(val rawValue: String) {
    GRAB_BIKE("GrabBike"),
    GRAB_CAR("GrabCar")
}