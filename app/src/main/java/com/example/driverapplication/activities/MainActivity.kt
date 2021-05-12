package com.example.driverapplication.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.common.AccountManager
import com.example.driverapplication.common.CommonUtils
import com.example.driverapplication.common.Constants
import com.example.driverapplication.databinding.ActivityMainBinding
import com.example.driverapplication.firebase.FirebaseConstants
import com.example.driverapplication.firebase.FirebaseManager
import com.example.driverapplication.fragments.BookFragment
import com.example.driverapplication.googlemaps.MapsConnection
import com.example.driverapplication.model.BookInfo
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.MainViewModel
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel
            by lazy {
                ViewModelProvider(this, BaseViewModelFactory(this)).get(
                    MainViewModel::class.java
                )
            }

    private val accountManager: AccountManager
            by lazy {
                AccountManager.getInstance()
            }

    private var fragmentBook : Fragment? = null
    var currentFragment = Constants.FRAGMENT_MAP

    private lateinit var transaction: FragmentTransaction

    // The entry point to the Fused Location Provider.
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel

        if (intent.hasExtra(Constants.NOTIFICATION_CONTENT)) {
            val jsonData = intent.getStringExtra(Constants.NOTIFICATION_CONTENT)!!
            val jsonObject = JSONObject(jsonData)
            Log.d("NamTV", "notify = ${jsonObject.getString(FirebaseConstants.KEY_USER_ID)}")
            mainViewModel.isShowMapLayout.set(false)
            getInfoUserBook(jsonObject)
            gotoBookFragment()
        }

        // TODO debug code
        accountManager.saveDriverId("idDriver_1")

        initDataMap()
        initView()
        accountManager.getTokenIdDevice {  }
    }

    private fun initDataMap() {
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initView() {
        // Build the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        transaction = supportFragmentManager.beginTransaction()

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("NamTV", "onMapReady")
        map = googleMap

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        if (locationPermissionGranted) {
            // Get the current location of the device and set the position of the map.
            getDeviceLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    getDeviceLocation()
                    updateLocationUI()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if (currentFragment == Constants.FRAGMENT_BOOK) {
            currentFragment = Constants.FRAGMENT_MAP
            mainViewModel.isShowMapLayout.set(true)
            gotoMapFragment()
            return
        }

        super.onBackPressed()
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationRequest = LocationRequest.create()
                locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                locationRequest.interval = (15*1000).toLong()
                locationRequest.fastestInterval = (15*1000).toLong()
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
                        if (locationResult == null) {
                            return
                        }
                        for (location in locationResult.locations) {
                            if (location != null) {
                                val currentLocation = LatLng(location.latitude, location.longitude)
                                accountManager.setLocationDriver(currentLocation)
                                map?.moveCamera(
                                    CameraUpdateFactory.newLatLng(currentLocation)
                                )
                            }
                        }
                    }
                }

                fusedLocationProviderClient?.lastLocation?.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        val currentLocation = LatLng(location.latitude, location.longitude)
                        accountManager.setLocationDriver(currentLocation)
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation, Constants.DEFAULT_ZOOM_MAPS.toFloat()
                            )
                        )
                        fusedLocationProviderClient?.requestLocationUpdates(locationRequest, locationCallback, null)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("NamTV", "MainActivity::getDeviceLocation: SecurityException: $e")
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            locationPermissionGranted = true
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                accountManager.setLocationDriver(Constants.DEFAULT_LOCATION)
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("NamTV", "MainActivity::updateLocationUI: SecurityException: $e")
        }
    }

    private fun gotoBookFragment() {
        fragmentBook = BookFragment()
        currentFragment = Constants.FRAGMENT_BOOK

        val transaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.add(R.id.fragmentBook, fragmentBook as BookFragment).commit()
        mainViewModel.isShowMapLayout.set(false)
    }

    private fun getInfoUserBook(jsonObject: JSONObject) {
        val bookInfo = BookInfo(
            userId = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_USER_ID),
            tokenId = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_TOKEN_ID),
            name = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_NAME),
            age = CommonUtils.getIntFromJsonObject(jsonObject, FirebaseConstants.KEY_AGE),
            sex = CommonUtils.getSexValue(CommonUtils.getIntFromJsonObject(jsonObject, FirebaseConstants.KEY_SEX)),
            phoneNumber = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_PHONE_NUMBER),
            startAddress = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_START_ADDRESS),
            endAddress = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_END_ADDRESS),
            price = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_PRICE),
            distance = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_DISTANCE)
        )
        mainViewModel.bookInfo = bookInfo
    }

    private fun drawShortestWayToUser() {
        FirebaseManager.getInstance().getLocationUser(mainViewModel.bookInfo!!.userId) { latitude, longitude ->
            MapsConnection.getInstance().drawShortestWay(map!!, latitude, longitude) {
                addMarkerUser(latitude, longitude)
            }
            gotoMapFragment()
        }
    }

    fun handleEventAgreeBook() {
        mainViewModel.isShowingLayoutBottom.set(true)
        drawShortestWayToUser()
    }

    fun gotoMapFragment() {
        currentFragment = Constants.FRAGMENT_MAP
        fragmentBook = null
        for (fragment in supportFragmentManager.fragments) {
            if (fragment !is SupportMapFragment) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
        mainViewModel.isShowMapLayout.set(true)
    }

    private fun addMarkerUser(latitude: Double, longitude: Double) {
        val markerOption = MarkerOptions().apply {
            position(LatLng(latitude, longitude))
            icon(bitmapFromVector(R.drawable.person))
        }
        map!!.addMarker(markerOption)
    }

    private fun bitmapFromVector(vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
                0,
                0,
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}