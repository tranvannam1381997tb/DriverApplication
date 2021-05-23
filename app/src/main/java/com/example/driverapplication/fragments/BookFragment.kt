package com.example.driverapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.activities.MainActivity
import com.example.driverapplication.common.AccountManager
import com.example.driverapplication.common.setOnSingleClickListener
import com.example.driverapplication.connection.HttpConnection
import com.example.driverapplication.databinding.FragmentBookBinding
import com.example.driverapplication.firebase.FirebaseConnection
import com.example.driverapplication.googlemaps.MapsConnection
import com.example.driverapplication.model.DriverInfoKey
import com.example.driverapplication.model.DriverStatus
import com.example.driverapplication.common.AppPreferences
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.MainViewModel
import org.json.JSONObject

class BookFragment : Fragment() {
    private val bookViewModel: MainViewModel
            by lazy {
                ViewModelProvider(requireActivity(), BaseViewModelFactory(requireContext())).get(MainViewModel::class.java)
            }

    private lateinit var binding: FragmentBookBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book, container, false)
        val view = binding.root
        binding.viewModel = bookViewModel
        setupEvent()
        return view
    }

    private fun setupEvent() {
        binding.btnAgree.setOnSingleClickListener(View.OnClickListener {
            val currentLocation = AccountManager.getInstance().getLocationDriver()
            MapsConnection.getInstance().getShortestWay(currentLocation.latitude, currentLocation.longitude, bookViewModel.bookInfo!!.latStart, bookViewModel.bookInfo!!.lngStart) { isSuccess, timeArrivedOrigin ->
                if (isSuccess) {
                    AppPreferences.getInstance(requireActivity()).saveBookInfoToPreferences(bookViewModel.bookInfo!!)
                    if (activity is MainActivity) {
                        (activity as MainActivity).handleEventAgreeBook(timeArrivedOrigin)
                    }
                }
            }

            HttpConnection.getInstance().startArriving(getJSONArriving()) { isSuccess, dataResponse->
                if (isSuccess) {
                    // TODO
                }
            }
        })

        binding.btnCancel.setOnSingleClickListener(View.OnClickListener {
            FirebaseConnection.getInstance().pushNotifyRejectBook(bookViewModel.bookInfo!!.tokenId) {
                if (activity is MainActivity) {
                    (activity as MainActivity).gotoMapFragment()
                }
            }
        })
    }

    private fun getJSONArriving(): JSONObject {
        val jsonBody = JSONObject()
        jsonBody.put(DriverInfoKey.KeyStatus.rawValue, DriverStatus.StatusArrivingOrigin.rawValue)
        return jsonBody
    }
}