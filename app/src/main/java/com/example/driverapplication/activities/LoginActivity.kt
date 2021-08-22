package com.example.driverapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.common.*
import com.example.driverapplication.connection.HttpConnection
import com.example.driverapplication.customviews.ConfirmDialog
import com.example.driverapplication.databinding.ActivityLoginBinding
import com.example.driverapplication.firebase.FirebaseConstants
import com.example.driverapplication.manager.AccountManager
import com.example.driverapplication.model.DriverInfoKey
import com.example.driverapplication.model.SexValue
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.LoginViewModel
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel
        by lazy {
            ViewModelProvider(this, BaseViewModelFactory(this)).get(LoginViewModel::class.java)
        }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = loginViewModel
        setEventView()
    }

    private fun setEventView() {
        binding.edtPhoneNumber.apply {
            afterTextChanged {
                loginViewModel.validateDataLogin(
                    binding.edtPhoneNumber.text.toString(),
                    binding.edtPassword.text.toString()
                )
            }

            onEditorActionNext {
                binding.edtPassword.requestFocus()
            }
        }

        binding.edtPassword.apply {
            afterTextChanged {
                loginViewModel.validateDataLogin(
                    binding.edtPhoneNumber.text.toString(),
                    binding.edtPassword.text.toString()
                )
            }

            onEditorActionDone {
                if (loginViewModel.isEnableBtnLogin.get()!!) {
                    startLogin()
                }
            }
        }

        binding.login.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            startLogin()
        }

        // TODO debug code
        binding.edtPhoneNumber.setText("0976356358")
        binding.edtPassword.setText("123456")
    }

    private fun startLogin() {
        HttpConnection.getInstance().startLogin(getJSONLogin()) { isSuccess, dataResponse ->
            if (isSuccess) {
                val jsonObject = JSONObject(dataResponse)

                if (saveDriverInfo(jsonObject)) {
                    startMainActivity()
                } else {
                    showDialogError(getString(R.string.login_false))
                }
            } else {
                showDialogError(dataResponse)
            }
        }
    }

    private fun getJSONLogin(): JSONObject {
        val jsonBody = JSONObject()
        val phoneNumber = binding.edtPhoneNumber.text.toString()
        if (phoneNumber.startsWith("0")) {
            val phoneNumberFormat = "+84" + phoneNumber.substring(1, phoneNumber.length)
            jsonBody.put(DriverInfoKey.KeyPhoneNumber.rawValue, phoneNumberFormat)
        } else {
            jsonBody.put(DriverInfoKey.KeyPhoneNumber.rawValue, phoneNumber)
        }
        jsonBody.put(DriverInfoKey.KeyPassword.rawValue, binding.edtPassword.text.toString())
        return jsonBody
    }

    private fun saveDriverInfo(jsonObject: JSONObject): Boolean {
        val driverInfo = CommonUtils.getJsonObjectFromJsonObject(jsonObject, FirebaseConstants.KEY_DRIVER)

        val driverId = CommonUtils.getStringFromJsonObject(driverInfo, DriverInfoKey.KeyDriverId.rawValue)
        if (driverId.isEmpty()) {
            return false
        }
        val accountManager = AccountManager.getInstance()
        accountManager.saveDriverId(driverId)

        val name = CommonUtils.getStringFromJsonObject(driverInfo, DriverInfoKey.KeyName.rawValue)
        val age = CommonUtils.getIntFromJsonObject(driverInfo, DriverInfoKey.KeyAge.rawValue)
        val sex = CommonUtils.getIntFromJsonObject(driverInfo, DriverInfoKey.KeySex.rawValue)
        val sexValue = if (sex == 0) {
            SexValue.MALE.rawValue
        } else {
            SexValue.FEMALE.rawValue
        }
        val phoneNumber = CommonUtils.getStringFromJsonObject(driverInfo, DriverInfoKey.KeyPhoneNumber.rawValue)
        val phoneNumberValue = if (phoneNumber.startsWith("+84")) {
            "0" + phoneNumber.substring(3, phoneNumber.length)
        } else {
            phoneNumber
        }
        val status = CommonUtils.getIntFromJsonObject(driverInfo, DriverInfoKey.KeyStatus.rawValue)
        val rate = CommonUtils.getFloatFromJsonObject(driverInfo, DriverInfoKey.KeyRate.rawValue)
        val startDate = CommonUtils.getDateFromJsonObject(driverInfo, DriverInfoKey.KeyStartDate.rawValue)
        val typeDriver = CommonUtils.getTypeDriver(driverInfo, DriverInfoKey.KeyTypeDriver.rawValue)
        val typeVehicle = CommonUtils.getStringFromJsonObject(driverInfo, DriverInfoKey.KeyTypeVehicle.rawValue)
        val licensePlateNumber = CommonUtils.getStringFromJsonObject(driverInfo, DriverInfoKey.KeyLicensePlateNumber.rawValue)

        accountManager.setDriverInfo(name, age, sexValue, phoneNumberValue, status, rate, startDate, typeDriver, typeVehicle, licensePlateNumber)

        return true
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun showDialogError(message: String?) {
        if (!message.isNullOrEmpty()) {
            val dialogError = ConfirmDialog(this)
            dialogError.setTextDisplay(getString(R.string.error), message, null, getString(R.string.ok))
            dialogError.setOnClickOK(View.OnClickListener {
                dialogError.dismiss()
            })
            dialogError.setHideLineButton()
            dialogError.show()
        }
    }
}