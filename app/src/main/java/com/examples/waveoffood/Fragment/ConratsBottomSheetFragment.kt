package com.examples.waveoffood.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.waveoffood.databinding.FragmentConratsBottomSheetBinding
import com.examples.waveoffood.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ConratsBottomSheetFragment : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentConratsBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //
        binding =  FragmentConratsBottomSheetBinding.inflate(layoutInflater, container, false)

        binding.goHome.setOnClickListener{
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // or shorter: flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(intent)
            dismiss()
        }

        return binding.root
    }
}