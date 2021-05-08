package com.example.grabapplication.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.activities.MainActivity
import com.example.driverapplication.common.AccountManager
import com.example.driverapplication.common.CommonUtils
import com.example.driverapplication.common.SexValue
import com.example.driverapplication.common.setOnSingleClickListener
import com.example.driverapplication.connection.HttpConnection
import com.example.driverapplication.databinding.FragmentInputInfoBinding
import com.example.driverapplication.model.DriverInfoKey
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.SignUpViewModel
import org.json.JSONObject

class InputInfoFragment : Fragment() {

    private val inputInfoViewModel: SignUpViewModel
            by lazy {
                ViewModelProvider(requireActivity(), BaseViewModelFactory(requireContext())).get(
                    SignUpViewModel::class.java)
            }

    private lateinit var binding: FragmentInputInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_info, container, false)
        val view = binding.root
        binding.viewModel = inputInfoViewModel
        setEventListener()
        return view
    }

    private fun setEventListener() {
        binding.btnNextInputInfo.setOnSingleClickListener(View.OnClickListener {
            if (validateInfo()) {
                inputInfoViewModel.name = binding.edtName.text.toString()
                inputInfoViewModel.age = binding.edtAge.text.toString().toInt()
                inputInfoViewModel.sex = if (inputInfoViewModel.isCheckMale.get()!!) SexValue.MALE.rawValue else SexValue.FEMALE.rawValue
                HttpConnection.getInstance().startSignUp(getJSONInfo()) { isSuccess, dataResponse ->
                    if (isSuccess) {
                        val jsonObject = JSONObject(dataResponse)
                        val driverId = CommonUtils.getStringFromJsonObject(jsonObject, DriverInfoKey.KeyDriverId.rawValue)
                        val accountManager = AccountManager.getInstance()
                        accountManager.saveDriverId(driverId)
                        startMainActivity()
                    } else {
                        showToastError(dataResponse)
                    }
                }
            }
        })
    }

    private fun getJSONInfo(): JSONObject {
        val jsonInfo = JSONObject()
        if (inputInfoViewModel.phoneNumber!!.startsWith("0")) {
            val phoneNumber = "+84" + inputInfoViewModel.phoneNumber!!.substring(1, inputInfoViewModel.phoneNumber!!.length)
            jsonInfo.put(DriverInfoKey.KeyPhoneNumber.rawValue, phoneNumber)
        } else {
            jsonInfo.put(DriverInfoKey.KeyPhoneNumber.rawValue, inputInfoViewModel.phoneNumber)
        }
        jsonInfo.put(DriverInfoKey.KeyPassword.rawValue, inputInfoViewModel.password)
        jsonInfo.put(DriverInfoKey.KeyName.rawValue, inputInfoViewModel.name)
        jsonInfo.put(DriverInfoKey.KeyAge.rawValue, inputInfoViewModel.age)
        jsonInfo.put(DriverInfoKey.KeySex.rawValue, inputInfoViewModel.sex)
        return jsonInfo
    }

    private fun validateInfo(): Boolean {
        if (binding.edtName.text.isNullOrEmpty()) {
            showToastError(getString(R.string.error_input_name))
            return false
        }
        if (binding.edtAge.text.isNullOrEmpty()) {
            showToastError(getString(R.string.error_input_age))
            return false
        }
        return true
    }

    private fun showToastError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
    }

    private fun startMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        context?.startActivity(intent)
    }
}