package com.example.driverapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.activities.MainActivity
import com.example.driverapplication.common.setOnSingleClickListener
import com.example.driverapplication.databinding.FragmentGoingBinding
import com.example.driverapplication.firebase.FirebaseConnection
import com.example.driverapplication.googlemaps.MapsConnection
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.MainViewModel
import com.google.android.gms.maps.model.LatLng

class GoingFragment : Fragment() {
    private val goingViewModel: MainViewModel
            by lazy {
                ViewModelProvider(requireActivity(), BaseViewModelFactory(requireContext())).get(
                    MainViewModel::class.java)
            }

    private lateinit var binding: FragmentGoingBinding
    private var currentStatus = STATUS_GOING_PICK_UP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_going, container, false)
        val view = binding.root
        binding.viewModel = goingViewModel
        updateLayout()
        setupEvent()
        return view
    }

    private fun setupEvent() {
        binding.btnArrived.setOnSingleClickListener(View.OnClickListener {
            when (currentStatus) {
                STATUS_GOING_PICK_UP -> handleGoingPickUp()

                STATUS_ARRIVED_ORIGIN -> handleArrivedOrigin()

                STATUS_GOING -> handleGoing()

                STATUS_ARRIVED_DESTINATION -> handleArrivedDestination()
            }

        })
    }

    private fun updateLayout() {
        val bundle = arguments
        if (bundle != null) {
            currentStatus = bundle.getInt(STATUS_GOING_FRAGMENT)
            when (currentStatus) {
                STATUS_GOING_PICK_UP -> updateLayoutGoingPickUp()

                STATUS_ARRIVED_ORIGIN -> updateLayoutArrivedOrigin()

                STATUS_GOING -> updateLayoutGoing()

                STATUS_ARRIVED_DESTINATION -> updateLayoutArrivedDestination()
            }
        }
    }

    private fun updateLayoutGoingPickUp() {
        binding.description.setText(R.string.description_arrived_origin)
        binding.btnArrived.setText(R.string.arrived_origin)
    }

    private fun updateLayoutArrivedOrigin() {
        binding.description.setText(R.string.description_waiting_user)
        binding.btnArrived.setText(R.string.going)
    }

    private fun updateLayoutGoing() {
        binding.description.setText(R.string.description_going)
        binding.btnArrived.setText(R.string.arrived_destination)
    }

    private fun updateLayoutArrivedDestination() {
        binding.description.setText(R.string.description_final_book)
        binding.btnArrived.setText(R.string.bill)
    }

    private fun handleGoingPickUp() {
        MapsConnection.getInstance().getShortestWay(goingViewModel.bookInfo!!.latStart, goingViewModel.bookInfo!!.lngStart, goingViewModel.bookInfo!!.latEnd, goingViewModel.bookInfo!!.lngEnd) { isSuccess, timeArrivedDestination ->
            if (isSuccess) {
                if (activity is MainActivity) {
                    (activity as MainActivity).handleEventArrivedOrigin(timeArrivedDestination)
                }
            }
        }
    }

    private fun handleArrivedOrigin() {
        FirebaseConnection.getInstance().pushNotifyGoing(goingViewModel.bookInfo!!.tokenId) { isSuccess ->
            if (isSuccess) {
                if (activity is MainActivity) {
                    (activity as MainActivity).handleEventGoing()
                }
            } else {
                // TODO
            }
        }
    }

    private fun handleGoing() {
        FirebaseConnection.getInstance().pushNotifyArrivedDestination(goingViewModel.bookInfo!!.tokenId) { isSuccess ->
            if (isSuccess) {
                if (activity is MainActivity) {
                    (activity as MainActivity).handleEventArrivedDestination()
                }
            } else {
                // TODO
            }
        }
    }

    private fun handleArrivedDestination() {
        FirebaseConnection.getInstance().pushNotifyBill(goingViewModel.bookInfo!!.tokenId) { isSuccess ->
            if (isSuccess) {
                if (activity is MainActivity) {
                    (activity as MainActivity).handleEventBill()
                }
            } else {
                // TODO
            }
        }
    }

    companion object {
        const val STATUS_GOING_FRAGMENT = "statusGoingFragment"
        const val STATUS_GOING_PICK_UP = 0
        const val STATUS_ARRIVED_ORIGIN = 1
        const val STATUS_GOING = 2
        const val STATUS_ARRIVED_DESTINATION = 3

    }
}