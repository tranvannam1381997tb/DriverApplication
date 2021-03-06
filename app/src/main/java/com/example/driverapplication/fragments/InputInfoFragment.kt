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
import com.example.driverapplication.databinding.FragmentInputInfoBinding
import com.example.driverapplication.models.SexValue
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.SignUpViewModel

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

                inputInfoViewModel.onClickSignUpScreenListener?.clickBtnNextInputInfo()
            }
        })
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
}