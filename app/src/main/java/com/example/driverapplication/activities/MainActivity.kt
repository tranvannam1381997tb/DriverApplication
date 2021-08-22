package com.example.driverapplication.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.common.*
import com.example.driverapplication.connection.HttpConnection
import com.example.driverapplication.customviews.ConfirmDialog
import com.example.driverapplication.databinding.ActivityMainBinding
import com.example.driverapplication.firebase.FirebaseConnection
import com.example.driverapplication.firebase.FirebaseConstants
import com.example.driverapplication.fragments.BillFragment
import com.example.driverapplication.fragments.BookFragment
import com.example.driverapplication.fragments.GoingFragment
import com.example.driverapplication.googlemaps.MapsConnection
import com.example.driverapplication.manager.AccountManager
import com.example.driverapplication.model.BookInfo
import com.example.driverapplication.services.BookListener
import com.example.driverapplication.services.DriverFirebaseMessagingService
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.MainViewModel
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mainMap: GoogleMap? = null
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
    private var currentFragment = Constants.FRAGMENT_MAP

    private lateinit var transaction: FragmentTransaction

    // The entry point to the Fused Location Provider.
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    private var locationPermissionGranted = false

    private var markerOrigin: Marker? = null
    private var markerDestination: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel

        initDataMap()
        initView()
        setupEvent()
        setupDrawerLayout()
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

    }

    private fun setupEvent() {
        DriverFirebaseMessagingService.bookListener = object : BookListener {

            override fun handleBookRequest(jsonData: JSONObject) {
                this@MainActivity.runOnUiThread {
                    if (!mainViewModel.isShowingLayoutBottom.get()!!) {
                        Log.d("NamTV", "notify = ${jsonData.getString(FirebaseConstants.KEY_USER_ID)}")
                        getInfoUserBook(jsonData)
                        CommonUtils.vibrateDevice()
                        gotoBookFragment()
                    }
                }
            }

            override fun handleCancelBook(jsonData: JSONObject) {
                this@MainActivity.runOnUiThread {
                    if (mainViewModel.bookInfo != null) {
                        val userId = jsonData.getString(FirebaseConstants.KEY_USER_ID)
                        if (userId == mainViewModel.bookInfo!!.userId) {
                            showDialogUserCancelBook()
                        }
                    }
                }
            }
        }
        binding.imgInfo.setOnSingleClickListener(View.OnClickListener {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            binding.drawerLayout.openDrawer(binding.menuLeft)
        })

        mainViewModel.onItemClickListener = object : MainViewModel.OnItemClickListener {
            override fun clickIconPhone() {
                if (mainViewModel.bookInfo != null) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:${mainViewModel.bookInfo!!.phoneNumber}")
                    startActivity(callIntent)
                }
            }

        }
    }

    private fun setupDrawerLayout() {
        findViewById<TextView>(R.id.layoutLeftTxtName).text = accountManager.getName()
        findViewById<TextView>(R.id.layoutLeftRate).text = accountManager.getRate().toString()
        findViewById<TextView>(R.id.layoutLeftTxtAge).text = getString(R.string.age_info, accountManager.getAge().toString())
        findViewById<TextView>(R.id.layoutLeftTxtSex).text = getString(R.string.sex_info, accountManager.getSex())
        findViewById<TextView>(R.id.layoutLeftTxtPhoneNumber).text = getString(R.string.phone_number_info, accountManager.getPhoneNumber())
        findViewById<TextView>(R.id.layoutLeftStartDate).text = getString(R.string.start_date_info, accountManager.getStartDate())
        findViewById<TextView>(R.id.layoutLeftTypeVehicle).text = accountManager.getTypeVehicle()
        findViewById<TextView>(R.id.layoutLeftLicensePlateNumber).text = accountManager.getLicensePlateNumber()
        findViewById<TextView>(R.id.layoutLeftTypeDriver).text = accountManager.getTypeDriver()
        findViewById<ImageView>(R.id.imgLogout).setOnSingleClickListener(View.OnClickListener {
            HttpConnection.getInstance().logout {
                Log.d("NamTV", "logout = $it")
                if (it) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("NamTV", "onMapReady")
        mainMap = googleMap

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
            gotoMapFragment()
        }
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
                                mainMap?.moveCamera(
                                    CameraUpdateFactory.newLatLng(currentLocation)
                                )

                                HttpConnection.getInstance().updateStatusDriver()
                                Log.d("NamTV", "update location LocationCallback $currentLocation")
                            }
                        }
                    }
                }

                fusedLocationProviderClient?.lastLocation?.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        val currentLocation = LatLng(location.latitude, location.longitude)
                        accountManager.setLocationDriver(currentLocation)
                        mainMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation, Constants.DEFAULT_ZOOM_MAPS.toFloat()
                            )
                        )
                        fusedLocationProviderClient?.requestLocationUpdates(locationRequest, locationCallback, null)
                        Log.d("NamTV", "update location fusedLocationProviderClient")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("NamTV", "MainActivity::getDeviceLocation: SecurityException: $e")
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE
                ),
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
        if (mainMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mainMap?.isMyLocationEnabled = true
                mainMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mainMap?.isMyLocationEnabled = false
                mainMap?.uiSettings?.isMyLocationButtonEnabled = false
                accountManager.setLocationDriver(Constants.DEFAULT_LOCATION)
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("NamTV", "MainActivity::updateLocationUI: SecurityException: $e")
        }
    }

    private fun getInfoUserBook(jsonObject: JSONObject) {
        val bookInfo = BookInfo(
            userId = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_USER_ID),
            tokenId = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_TOKEN_ID),
            name = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_NAME),
            age = CommonUtils.getIntFromJsonObject(jsonObject, FirebaseConstants.KEY_AGE),
            sex = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_SEX),
            phoneNumber = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_PHONE_NUMBER),
            startAddress = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_START_ADDRESS),
            endAddress = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_END_ADDRESS),
            latStart = CommonUtils.getDoubleFromJsonObject(jsonObject, FirebaseConstants.KEY_LAT_START),
            lngStart = CommonUtils.getDoubleFromJsonObject(jsonObject, FirebaseConstants.KEY_LNG_START),
            latEnd = CommonUtils.getDoubleFromJsonObject(jsonObject, FirebaseConstants.KEY_LAT_END),
            lngEnd = CommonUtils.getDoubleFromJsonObject(jsonObject, FirebaseConstants.KEY_LNG_END),
            price = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_PRICE),
            distance = CommonUtils.getStringFromJsonObject(jsonObject, FirebaseConstants.KEY_DISTANCE)
        )
        mainViewModel.bookInfo = bookInfo
    }

    private fun drawShortestWayToUser() {
        mainMap!!.clear()
        val polyLines = MapsConnection.getInstance().polylines
        for (i in 0 until polyLines.size) {
            mainMap!!.addPolyline(PolylineOptions().addAll(polyLines[i]).color(Color.RED))
        }
        val latLngStart = LatLng(mainViewModel.bookInfo!!.latStart, mainViewModel.bookInfo!!.lngStart)
        val currentLocation = AccountManager.getInstance().getLocationDriver()
        addMarkerDestination(currentLocation, latLngStart)
    }

    private fun drawShortestWayToDestination() {
        mainMap!!.clear()
        val polyLines = MapsConnection.getInstance().polylines
        for (i in 0 until polyLines.size) {
            mainMap!!.addPolyline(PolylineOptions().addAll(polyLines[i]).color(Color.RED))
        }
        val latLngStart = LatLng(mainViewModel.bookInfo!!.latStart, mainViewModel.bookInfo!!.lngStart)
        val latLngEnd = LatLng(mainViewModel.bookInfo!!.latEnd, mainViewModel.bookInfo!!.lngEnd)
        addMarkerDestination(latLngStart, latLngEnd)
    }

    fun handleEventAgreeBook(timeArrivedOrigin: Int) {
        FirebaseConnection.getInstance().pushNotifyAgreeBook(mainViewModel.bookInfo!!.tokenId, timeArrivedOrigin) { isSuccess ->
            if (isSuccess) {
                drawShortestWayToUser()
                mainViewModel.isShowingLayoutBook.set(false)
                mainViewModel.isShowingLayoutBottom.set(true)
                gotoGoingFragment(GoingFragment.STATUS_ARRIVING_ORIGIN)
            } else {
                // TODO
            }
        }

    }

    fun handleEventArrivedOrigin(startAddress: String, timeArrivedDestination: Int) {
        FirebaseConnection.getInstance().pushNotifyArrivedOrigin(mainViewModel.bookInfo!!.tokenId, startAddress, timeArrivedDestination) { isSuccess ->
            if (isSuccess) {
                mainViewModel.isShowingLayoutBottom.set(true)
                gotoGoingFragment(GoingFragment.STATUS_ARRIVED_ORIGIN)
                drawShortestWayToDestination()
            } else {
                // TODO
            }
        }
    }

    fun handleEventGoing() {
        mainViewModel.isShowingLayoutBottom.set(true)
        gotoGoingFragment(GoingFragment.STATUS_ARRIVING_DESTINATION)
    }

    fun handleEventArrivedDestination() {
        mainViewModel.isShowingLayoutBottom.set(true)
        gotoGoingFragment(GoingFragment.STATUS_ARRIVED_DESTINATION)
        mainMap!!.clear()
    }

    fun handleEventBill() {
        gotoFragmentBill()
    }

    fun gotoMapFragment() {
        currentFragment = Constants.FRAGMENT_MAP
        mainViewModel.isShowingLayoutBottom.set(false)
        mainViewModel.isShowingLayoutBook.set(false)
        fragmentBook = null
        for (fragment in supportFragmentManager.fragments) {
            if (fragment !is SupportMapFragment) {
                supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            }
        }
    }

    private fun gotoBookFragment() {
        fragmentBook = BookFragment()
        currentFragment = Constants.FRAGMENT_BOOK
        mainViewModel.isShowingLayoutBook.set(true)
        mainViewModel.isShowingLayoutBottom.set(false)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_top,
            R.anim.pop_in_bottom,
            R.anim.pop_out_top
        )
        transaction.replace(R.id.fragmentBook, fragmentBook as BookFragment).commitAllowingStateLoss()
    }

    private fun gotoGoingFragment(statusGoingFragment: Int) {
        fragmentBook = GoingFragment()
        currentFragment = Constants.FRAGMENT_GOING

        val bundle = Bundle()
        bundle.putInt(GoingFragment.STATUS_GOING_FRAGMENT, statusGoingFragment)
        fragmentBook!!.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_top,
            R.anim.pop_in_bottom,
            R.anim.pop_out_top
        )
        transaction.replace(R.id.fragmentBottom, fragmentBook as GoingFragment).commit()
    }

    private fun gotoFragmentBill() {
        fragmentBook = BillFragment()
        currentFragment = Constants.FRAGMENT_BILL
        mainViewModel.isShowingLayoutBottom.set(false)
        mainViewModel.isShowingLayoutBook.set(true)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_top,
            R.anim.pop_in_bottom,
            R.anim.pop_out_top
        )
        transaction.replace(R.id.fragmentBook, fragmentBook as BillFragment).commit()
    }

    private fun addMarkerDestination(latLngOrigin: LatLng, latLngDestination: LatLng) {
        val markerOptionDestination = MarkerOptions().apply {
            position(latLngDestination)
            icon(bitmapFromVector(R.drawable.person))
        }
        val markerOptionOrigin = MarkerOptions().apply {
            position(latLngOrigin)
        }
        markerOrigin = mainMap!!.addMarker(markerOptionOrigin)
        markerDestination = mainMap!!.addMarker(markerOptionDestination)
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

    private fun showDialogUserCancelBook() {
        val dialog = ConfirmDialog(this)
        dialog.setTextDisplay(
                getString(R.string.user_cancel_book),
                null,
                null,
                getString(R.string.ok)
        )
        dialog.setOnClickOK(View.OnClickListener {
            dialog.dismiss()
            gotoMapFragment()
            refreshScreen()
        })

        dialog.setTextTypeBoldBtnOK()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private fun refreshScreen() {
        mainMap?.clear()
        AppPreferences.getInstance(this).bookInfoPreferences = null
        mainViewModel.bookInfo = null
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}