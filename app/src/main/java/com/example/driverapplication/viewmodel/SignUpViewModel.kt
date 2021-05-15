package com.example.driverapplication.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class SignUpViewModel: ViewModel() {
    var name: String? = null
    var password: String? = null
    var age: Int? = null
    var sex: String? = null
    var phoneNumber: String? = null
    var typeVehicle: String? = null
    var licensePlateNumber: String? = null
    var typeDriver: String? = null

    var isCheckMale: ObservableField<Boolean> = ObservableField(true)
    var isCheckGrabBike: ObservableField<Boolean> = ObservableField(true)

    var onClickSignUpScreenListener: OnClickSignUpScreenListener? = null

    fun checkRadioMale() {
        isCheckMale.set(true)
    }

    fun checkRadioFemale() {
        isCheckMale.set(false)
    }

    fun checkRadioGrabBike() {
        isCheckGrabBike.set(true)
    }

    fun checkRadioGrabCar() {
        isCheckGrabBike.set(false)
    }
}

interface OnClickSignUpScreenListener {
    fun clickBtnNextInputPhoneNumber()

    fun clickBtnNextInputPassword()

    fun clickBtnNextInputInfo()
}