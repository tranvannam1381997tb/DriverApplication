package com.example.driverapplication.common

import android.util.Log
import com.example.driverapplication.firebase.FirebaseManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.iid.FirebaseInstanceId

class AccountManager private constructor() {

    private var idDriver: String? = null
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
        getTokenId {
            tokenId = it
        }
    }

    fun saveIdDriver(id: String) {
        idDriver = id
    }

    fun getIdDriver(): String{
        return idDriver ?: ""
    }

    private fun getTokenId(callback: (String?) -> Unit) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if(!it.isSuccessful){
                Log.e("NamTV", "getInstanceId failed", it.exception)
                return@addOnCompleteListener
            }
            val token =  it.result?.token
            callback.invoke(token)
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