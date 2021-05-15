package com.example.driverapplication.common

import android.util.Log
import com.example.driverapplication.firebase.FirebaseManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.iid.FirebaseInstanceId

class AccountManager private constructor() {

    private var driverId: String? = null
    private var tokenId: String? = null
    private var name: String? = null
    private var age: Int? = null
    private var sex: Int? = null
    private var phoneNumber: String? = null
    private var currentLocation: LatLng? = null
    private var rate: Double? = null
    private var status: Int? = null
    private var startDate: String? = null
    private var typeDriver: String? = null
    private var typeVehicle: String? = null
    private var licensePlateNumber: String? = null

    companion object {
        private var instance: AccountManager? = null

        fun getInstance(): AccountManager {
            if (instance == null) {
                synchronized(AccountManager::class.java) {
                    if (instance == null) {
                        instance = AccountManager()
                    }
                }
            }
            return instance!!
        }
    }

    init {
        getTokenIdDevice {
            tokenId = it
        }
    }

    fun saveDriverId(id: String) {
        driverId = id
    }

    fun getDriverId(): String{
        return driverId ?: ""
    }

    fun getTokenIdDevice(callback: (String?) -> Unit) {
        if (tokenId != null) {
            callback.invoke(tokenId)
            FirebaseManager.getInstance().updateTokenIdToFirebase(tokenId!!)
            return
        }
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if(!it.isSuccessful){
                Log.e("NamTV", "getInstanceId failed", it.exception)
                return@addOnCompleteListener
            }
            val token =  it.result?.token
            Log.d("NamTV", "$token")
            callback.invoke(token)
            FirebaseManager.getInstance().updateTokenIdToFirebase(token!!)
        }
    }

    fun setLocationDriver(location: LatLng) {
        currentLocation = location
        FirebaseManager.getInstance().updateLocationDriverToFirebase(location)
    }

    fun getLocationDriver(): LatLng {
        return currentLocation ?: Constants.DEFAULT_LOCATION
    }
}

enum class SexValue(val rawValue: String) {
    MALE("Nam"),
    FEMALE("Nữ")
}

enum class TypeDriverValue(val rawValue: String) {
    GRAB_BIKE("GrabBike"),
    GRAB_CAR("GrabCar")
}

enum class StatusDriver(val rawValue: Int) {
    OFF(-1),
    ON(0),
    ARRIVING(1),
    GOING(2)
}