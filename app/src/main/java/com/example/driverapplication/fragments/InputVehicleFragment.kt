package com.example.driverapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.activities.MainActivity
import com.example.driverapplication.common.*
import com.example.driverapplication.connection.HttpConnection
import com.example.driverapplication.databinding.FragmentInputVehicleBinding
import com.example.driverapplication.manager.AccountManager
import com.example.driverapplication.model.DriverInfoKey
import com.example.driverapplication.model.SexValue
import com.example.driverapplication.model.TypeDriverValue
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.SignUpViewModel
import org.json.JSONObject
import java.util.*

class InputVehicleFragment : Fragment() {

    private val inputVehicleViewModel: SignUpViewModel
            by lazy {
                ViewModelProvider(requireActivity(), BaseViewModelFactory(requireContext())).get(
                    SignUpViewModel::class.java)
            }

    private lateinit var binding: FragmentInputVehicleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_vehicle, container, false)
        val view = binding.root
        binding.viewModel = inputVehicleViewModel
        setEventListener()
        return view
    }

    private fun setEventListener() {
        binding.btnNextInputVehicle.setOnSingleClickListener(View.OnClickListener {
            if (validateInfoVehicle()) {
                inputVehicleViewModel.typeVehicle = binding.edtTypeVehicle.text.toString()
                inputVehicleViewModel.licensePlateNumber = binding.edtLicensePlateNumber.text.toString()
                inputVehicleViewModel.typeDriver = if (inputVehicleViewModel.isCheckGrabBike.get()!!) TypeDriverValue.GRAB_BIKE.rawValue else TypeDriverValue.GRAB_CAR.rawValue
                HttpConnection.getInstance().startSignUp(getJSONInfo()) { isSuccess, dataResponse ->
                    if (isSuccess) {
                        val jsonObject = JSONObject(dataResponse)
                        if (saveDriverInfo(jsonObject)) {
                            startMainActivity()
                        }
                    } else {
                        showToastError(dataResponse)
                    }
                }
            }
        })
    }

    private fun saveDriverInfo(jsonObject: JSONObject): Boolean {
        val driverId = CommonUtils.getStringFromJsonObject(jsonObject, DriverInfoKey.KeyDriverId.rawValue)
        if (driverId.isEmpty()) {
            return false
        }
        val accountManager = AccountManager.getInstance()
        accountManager.saveDriverId(driverId)

        val name = inputVehicleViewModel.name!!
        val age = inputVehicleViewModel.age!!
        val sex = inputVehicleViewModel.sex!!
        val phoneNumber = inputVehicleViewModel.phoneNumber!!
        val phoneNumberValue = if (phoneNumber.startsWith("+84")) {
            "0" + phoneNumber.substring(3, phoneNumber.length)
        } else {
            phoneNumber
        }
        val status = 0
        val rate = 5F
        val startDate = Calendar.getInstance().time.toString()
        val typeDriver = inputVehicleViewModel.typeDriver!!
        val typeVehicle = inputVehicleViewModel.typeVehicle!!
        val licensePlateNumber = inputVehicleViewModel.licensePlateNumber!!

        accountManager.setDriverInfo(name, age, sex, phoneNumberValue, status, rate, startDate, typeDriver, typeVehicle, licensePlateNumber)

        return true
    }

    private fun getJSONInfo(): JSONObject {
        val jsonInfo = JSONObject()
        if (inputVehicleViewModel.phoneNumber!!.startsWith("0")) {
            val phoneNumber = "+84" + inputVehicleViewModel.phoneNumber!!.substring(1, inputVehicleViewModel.phoneNumber!!.length)
            jsonInfo.put(DriverInfoKey.KeyPhoneNumber.rawValue, phoneNumber)
        } else {
            jsonInfo.put(DriverInfoKey.KeyPhoneNumber.rawValue, inputVehicleViewModel.phoneNumber)
        }
        jsonInfo.put(DriverInfoKey.KeyPassword.rawValue, inputVehicleViewModel.password)
        jsonInfo.put(DriverInfoKey.KeyName.rawValue, inputVehicleViewModel.name)
        jsonInfo.put(DriverInfoKey.KeyAge.rawValue, inputVehicleViewModel.age)
        if (inputVehicleViewModel.sex == SexValue.MALE.rawValue) {
            jsonInfo.put(DriverInfoKey.KeySex.rawValue, 0)
        } else {
            jsonInfo.put(DriverInfoKey.KeySex.rawValue, 1)
        }

        if (inputVehicleViewModel.typeDriver == TypeDriverValue.GRAB_BIKE.rawValue) {
            jsonInfo.put(DriverInfoKey.KeyTypeDriver.rawValue, 0)
        } else {
            jsonInfo.put(DriverInfoKey.KeyTypeDriver.rawValue, 1)

        }
        jsonInfo.put(DriverInfoKey.KeyTypeVehicle.rawValue, inputVehicleViewModel.typeVehicle)
        jsonInfo.put(DriverInfoKey.KeyLicensePlateNumber.rawValue, inputVehicleViewModel.licensePlateNumber)
        return jsonInfo
    }

    private fun validateInfoVehicle(): Boolean {
        if (binding.edtTypeVehicle.text.isNullOrEmpty()) {
            showToastError(getString(R.string.error_input_type_vehicle))
            return false
        }
        if (binding.edtLicensePlateNumber.text.isNullOrEmpty()) {
            showToastError(getString(R.string.error_input_license_plate_number))
            return false
        }
        return true
    }

    private fun showToastError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
    }

    private fun startMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}