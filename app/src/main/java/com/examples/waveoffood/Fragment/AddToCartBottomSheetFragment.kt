package com.examples.waveoffood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.waveoffood.R
import com.example.waveoffood.databinding.FragmentAddToCartBottomSheetBinding
import com.examples.waveoffood.Model.FoodItemModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddToCartBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddToCartBottomSheetBinding

    companion object {

        fun newInstance(foodItem: FoodItemModel): AddToCartBottomSheetFragment {

            val fragment = AddToCartBottomSheetFragment()

            val bundle = Bundle()
            bundle.putString("image", foodItem.itemImage)
            bundle.putString("name", foodItem.title)
            bundle.putString("price", foodItem.price)
            bundle.putString("description", foodItem.description)

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddToCartBottomSheetBinding.inflate(inflater, container, false)

        val image = arguments?.getString("image")
        val name = arguments?.getString("name")
        val price = arguments?.getString("price")
        val description = arguments?.getString("description")

        binding.textViewTitle.text = name
        binding.textViewRupees.text = price
        binding.textViewBottomDescription.text = description

        Glide.with(requireContext())
            .load(image)
            .into(binding.imageViewImage)

        return binding.root
    }
}