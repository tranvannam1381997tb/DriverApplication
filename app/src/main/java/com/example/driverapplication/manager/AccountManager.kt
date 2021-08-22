package com.example.driverapplication.manager

import com.example.driverapplication.common.Constants
import com.example.driverapplication.firebase.FirebaseManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.messaging.FirebaseMessaging

class AccountManager private constructor() {

    private var driverId: String? = null
    private var tokenId: String? = null
    private var name: String? = null
    private var age: Int? = null
    private var sex: String? = null
    private var phoneNumber: String? = null
    private var currentLocation: LatLng? = null
    private var rate: Float? = null
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

    fun getDriverId(): String {
        return driverId ?: ""
    }

    fun getTokenIdDevice(callback: (String?) -> Unit) {
        if (tokenId != null) {
            callback.invoke(tokenId)
            FirebaseManager.getInstance().updateTokenIdToFirebase(tokenId!!)
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = it.result
            if (token != null) {
                callback.invoke(token)
                FirebaseManager.getInstance().updateTokenIdToFirebase(token)
            }
        }
    }

    fun setLocationDriver(location: LatLng) {
        currentLocation = location
        FirebaseManager.getInstance().updateLocationDriverToFirebase(location)
    }

    fun getLocationDriver(): LatLng {
        return currentLocation ?: Constants.DEFAULT_LOCATION
    }

    fun setDriverInfo(name: String, age: Int, sex: String, phoneNumber: String, status: Int, rate: Float, startDate: String, typeDriver: String, typeVehicle: String, licensePlateNumber: String) {
        this.name = name
        this.age = age
        this.sex = sex
        this.phoneNumber = phoneNumber
        this.status = status
        this.rate = rate
        this.startDate = startDate
        this.typeDriver = typeDriver
        this.typeVehicle = typeVehicle
        this.licensePlateNumber = licensePlateNumber
    }

    fun getName(): String {
        return name!!
    }

    fun getSex(): String {
        return sex!!
    }

    fun getAge(): Int {
        return age!!
    }

    fun getPhoneNumber(): String {
        return phoneNumber!!
    }

    fun getRate(): Float {
        return rate!!
    }

    fun getStartDate(): String {
        return startDate!!
    }

    fun getTypeDriver(): String {
        return typeDriver!!
    }

    fun getTypeVehicle(): String {
        return typeVehicle!!
    }

    fun getLicensePlateNumber(): String {
        return licensePlateNumber!!
    }
}