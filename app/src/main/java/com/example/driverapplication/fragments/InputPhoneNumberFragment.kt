package com.example.driverapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.common.setOnSingleClickListener
import com.example.driverapplication.databinding.FragmentInputPhoneNumberBinding
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.SignUpViewModel

class InputPhoneNumberFragment : Fragment() {

    private val inputPhoneNumberViewModel: SignUpViewModel
            by lazy {
                ViewModelProvider(requireActivity(), BaseViewModelFactory(requireContext())).get(
                    SignUpViewModel::class.java)
            }

    private lateinit var binding: FragmentInputPhoneNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_phone_number, container, false)
        val view = binding.root
        binding.viewModel = inputPhoneNumberViewModel
        setUpEvent()
        return view
    }

    private fun setUpEvent(){
        binding.btnNextInputPhoneNumber.setOnSingleClickListener(View.OnClickListener {
            if (validatePhoneNumber(binding.edtPhoneNumber.text.toString())) {
                inputPhoneNumberViewModel.phoneNumber = binding.edtPhoneNumber.text.toString()
                inputPhoneNumberViewModel.onClickSignUpScreenListener?.clickBtnNextInputPhoneNumber()
            } else {
                showToastError(getString(R.string.error_phone_number_invalid))
            }
        })
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        if ((phoneNumber.startsWith("0") && phoneNumber.length == 10) || (phoneNumber.startsWith("+84") && phoneNumber.length == 12)) {
            return true
        }
        return false
    }

    private fun showToastError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
    }
}