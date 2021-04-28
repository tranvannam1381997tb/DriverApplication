package com.example.driverapplication.firebase

import com.example.driverapplication.common.AccountManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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

    val databaseDrivers: DatabaseReference
            by lazy {
                rootDb.child(FirebaseConstants.KEY_DRIVERS)
            }

    private val databaseUsers: DatabaseReference
            by lazy {
                rootDb.child(FirebaseConstants.KEY_USERS)
            }

    fun updateLocationDriverToFirebase(location: LatLng) {
        val idDriver = AccountManager.getInstance().getIdDriver()
        if (idDriver.isNotEmpty()) {
            databaseDrivers.child(idDriver).child(FirebaseConstants.KEY_LATITUDE).setValue(location.latitude)
            databaseDrivers.child(idDriver).child(FirebaseConstants.KEY_LONGITUDE).setValue(location.longitude)
        }
    }
}