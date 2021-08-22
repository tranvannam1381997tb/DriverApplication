package com.example.driverapplication.googlemaps

class MapsConstant {
    companion object {
        const val URL_DIRECTION = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s"
        const val STATUS_OK = "OK"

        // google directions api key
        const val DIRECTION_STATUS = "status"
        const val DIRECTION_ROUTES = "routes"
        const val DIRECTION_LEGS = "legs"
        const val DIRECTION_DURATION = "duration"
        const val DIRECTION_VALUE = "value"
        const val DIRECTION_STEPS = "steps"
        const val DIRECTION_POLYLINE = "polyline"
        const val DIRECTION_POINTS = "points"

        // google place api key
        const val PLACE_STATUS = "status"
    }
}