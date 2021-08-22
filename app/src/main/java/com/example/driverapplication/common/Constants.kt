package com.example.driverapplication.common

import com.google.android.gms.maps.model.LatLng

class Constants {
    companion object {
        const val DEFAULT_ZOOM_MAPS = 15

        const val DATE_FORMAT_FOR_FIREBASE = "dd/MM/yyyy"
        const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"
        const val DATE_FORMAT_APP = "dd, MMM yyyy"

        const val FRAGMENT_MAP = 0
        const val FRAGMENT_BOOK = 1
        const val FRAGMENT_GOING = 2
        const val FRAGMENT_BILL = 3

        val DEFAULT_LOCATION = LatLng(-33.8523341, 151.2106085)
    }
}