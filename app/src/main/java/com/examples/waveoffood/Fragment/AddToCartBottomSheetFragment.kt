package com.examples.waveoffood.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.FragmentAddToCartBottomSheetBinding
import com.examples.waveoffood.Model.CartItems
import com.examples.waveoffood.Model.FoodItemModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddToCartBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddToCartBottomSheetBinding

    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodPrice: String? = null
    private lateinit var auth: FirebaseAuth
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
        auth = FirebaseAuth.getInstance()

        foodImage = arguments?.getString("image")
        foodName = arguments?.getString("name")
        foodPrice = arguments?.getString("price")
        foodDescription = arguments?.getString("description")

        binding.textViewTitle.text = foodName
        binding.textViewRupees.text = foodPrice
        binding.textViewBottomDescription.text = foodDescription

        Glide.with(requireContext())
            .load(foodImage)
            .into(binding.imageViewImage)

        binding.addItem.setOnClickListener {
            addItemToCart()
        }

        return binding.root
    }

    private fun addItemToCart() {

        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: return

        val cartItems = CartItems(
            foodName = foodName,
            foodPrice = foodPrice,
            foodDescription = foodDescription,
            foodImage = foodImage,
            foodQuantity = 1
        )

        database.child("user").child(userId).child("CartItems").push()
            .setValue(cartItems)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Item added successfully 😆", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Item not added 😓", Toast.LENGTH_SHORT).show()
            }
    }
}