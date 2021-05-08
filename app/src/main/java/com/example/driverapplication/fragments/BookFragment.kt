package com.example.driverapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.driverapplication.R
import com.example.driverapplication.databinding.FragmentBookBinding
import com.example.driverapplication.viewmodel.BaseViewModelFactory
import com.example.driverapplication.viewmodel.MainViewModel

class BookFragment : Fragment() {
    private val bookViewModel: MainViewModel
            by lazy {
                ViewModelProvider(requireActivity(), BaseViewModelFactory(requireContext())).get(MainViewModel::class.java)
            }

    private lateinit var binding: FragmentBookBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book, container, false)
        val view = binding.root

        return view
    }

}