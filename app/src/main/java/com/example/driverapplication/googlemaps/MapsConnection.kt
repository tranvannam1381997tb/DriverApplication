package com.example.driverapplication.googlemaps

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.driverapplication.DriverApplication
import com.example.driverapplication.R
import com.example.driverapplication.common.CommonUtils
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import kotlin.collections.ArrayList

class MapsConnection private constructor() {

    val polylines : MutableList<List<LatLng>> = ArrayList()

    fun getShortestWay(startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double, callback: (Boolean, Int) -> Unit) {
        val urlDirections = getMapsApiDirectionsUrl(startLatitude, startLongitude, endLatitude, endLongitude)
        polylines.clear()
        val directionsRequest = object : StringRequest(
            Method.GET,
            urlDirections,
            Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                val status =
                    CommonUtils.getStringFromJsonObject(jsonResponse, MapsConstant.DIRECTION_STATUS)
                if (status == MapsConstant.STATUS_OK) {
                    // Get routes
                    val routes = CommonUtils.getJsonArrayFromJsonObject(
                        jsonResponse,
                        MapsConstant.DIRECTION_ROUTES
                    )
                    val legs = CommonUtils.getJsonArrayFromJsonObject(
                        routes.getJSONObject(0),
                        MapsConstant.DIRECTION_LEGS
                    )
                    val step = CommonUtils.getJsonArrayFromJsonObject(
                        legs.getJSONObject(0),
                        MapsConstant.DIRECTION_STEPS
                    )
                    for (i in 0 until step.length()) {
                        val polyline = CommonUtils.getJsonObjectFromJsonObject(
                            step.getJSONObject(i),
                            MapsConstant.DIRECTION_POLYLINE
                        )
                        val points = CommonUtils.getStringFromJsonObject(
                            polyline,
                            MapsConstant.DIRECTION_POINTS
                        )
                        polylines.add(PolyUtil.decode(points))
                    }

                    val duration = CommonUtils.getJsonObjectFromJsonObject(legs.getJSONObject(0), MapsConstant.DIRECTION_DURATION)

                    val time = CommonUtils.getIntFromJsonObject(duration, MapsConstant.DIRECTION_VALUE)

                    callback.invoke(true, time)
                }
            },
            Response.ErrorListener {
            }){}
        val requestQueue = Volley.newRequestQueue(DriverApplication.getAppContext())
        requestQueue.add(directionsRequest)
    }

    private fun getMapsApiDirectionsUrl(startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double): String {
        val originLocation = "$startLatitude,$startLongitude"
        val destinationLocation = "$endLatitude,$endLongitude"
        return String.format(
            MapsConstant.URL_DIRECTION,
            originLocation,
            destinationLocation,
            DriverApplication.getAppContext().getString(
                R.string.directions_api_key
            )
        )
    }

    companion object {
        private var instance: MapsConnection? = null
        fun getInstance(): MapsConnection {
            if (instance == null) {
                synchronized(MapsConnection::class.java) {
                    if (instance == null) {
                        instance = MapsConnection()
                    }
                }
            }
            return instance!!
        }
    }
}

interface GetDistanceListener {
    fun getDistance()
}