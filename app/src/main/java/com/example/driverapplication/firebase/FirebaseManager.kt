package com.example.driverapplication.firebase

import android.util.Log
import com.example.driverapplication.common.AccountManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*

class FirebaseManager private constructor() {

    companion object {
        private var instance: FirebaseManager? = null

        fun getInstance(): FirebaseManager {
            if (instance == null) {
                synchronized(FirebaseManager::class.java) {
                    if (instance == null) {
                        instance = FirebaseManager()
                    }
                }
            }
            return instance!!
        }
    }

    private val rootDb : DatabaseReference
            by lazy {
                FirebaseDatabase.getInstance().reference
            }

    private val databaseDrivers: DatabaseReference
            by lazy {
                rootDb.child(FirebaseConstants.KEY_DRIVERS)
            }

    val databaseUsers: DatabaseReference
            by lazy {
                rootDb.child(FirebaseConstants.KEY_USERS)
            }

    fun updateLocationDriverToFirebase(location: LatLng) {
        val idDriver = AccountManager.getInstance().getDriverId()
        if (idDriver.isNotEmpty()) {
            databaseDrivers.child(idDriver).child(FirebaseConstants.KEY_LATITUDE).setValue(location.latitude)
            databaseDrivers.child(idDriver).child(FirebaseConstants.KEY_LONGITUDE).setValue(location.longitude)
        }
    }

    fun updateTokenIdToFirebase(tokenId: String) {
        val idDriver = AccountManager.getInstance().getDriverId()
        if (idDriver.isNotEmpty()) {
            databaseDrivers.child(idDriver).child(FirebaseConstants.KEY_TOKEN_ID).setValue(tokenId)
            Log.d("NamTV", "token[$idDriver] = $tokenId")
        }
    }
}