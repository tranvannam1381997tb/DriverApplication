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
import com.example.driverapplication.model.DriverInfoKey
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.LoginViewModel
import kotlinx.coroutines.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel
        by lazy {
            ViewModelProvider(this, BaseViewModelFactory(this)).get(LoginViewModel::class.java)
        }

    private lateinit var binding: ActivityLoginBinding

    private var jobStartLogin: Job? = null

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
        binding.edtPhoneNumber.setText("0976356351")
        binding.edtPassword.setText("123456")
    }

    private fun startLogin() {
        HttpConnection.getInstance().startLogin(getJSONLogin()) { isSuccess, dataResponse ->
            if (isSuccess) {
                val jsonObject = JSONObject(dataResponse)
                val userId = CommonUtils.getStringFromJsonObject(jsonObject, DriverInfoKey.KeyDriverId.rawValue)
                val accountManager = AccountManager.getInstance()
                accountManager.saveDriverId(userId)
                startMainActivity()
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

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun showDialogError(message: String?) {
        if (!message.isNullOrEmpty()) {
            val dialogError = ConfirmDialog(this)
            dialogError.setTextDisplay(getString(R.string.error), message, null, getString(R.string.label_ok))
            dialogError.setOnClickOK(View.OnClickListener {
                dialogError.dismiss()
            })
            dialogError.setHideLineButton()
            dialogError.show()
        }
    }
}