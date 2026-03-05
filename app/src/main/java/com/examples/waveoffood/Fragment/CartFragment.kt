package com.examples.waveoffood.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.waveoffood.databinding.FragmentCartBinding
import com.examples.waveoffood.Adapter.CardAdapter
import com.examples.waveoffood.Model.CartItems
import com.examples.waveoffood.PaymentMethodActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartAdapter: CardAdapter   // ← rename if needed (CardAdapter → CartAdapter)
    private val cartItemsList = mutableListOf<CartItems>()   // ← single source of truth
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""

        retrieveCartItems()

        binding.payNowButton.setOnClickListener{
            if (cartAdapter.itemCount == 0) {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getOrderItemsDetails()
        }
        return binding.root
    }

    private fun retrieveCartItems() {
        val foodReference = database.reference
            .child("user")
            .child(userId)
            .child("CartItems")

        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItemsList.clear()

                for (foodSnapshot in snapshot.children) {
                    val cartItem = foodSnapshot.getValue(CartItems::class.java)
                    if (cartItem != null) {
                        cartItem.key = foodSnapshot.key   // ← crucial for delete
                        cartItemsList.add(cartItem)
                    }
                }

                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load cart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter() {
        cartAdapter = CardAdapter(requireContext(), cartItemsList)   // now matches!
        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
        }
    }

    private fun getOrderItemsDetails() {
        // No need to re-fetch from Firebase — use the local list (already up-to-date)
        val foodName = cartItemsList.mapNotNull { it.foodName }.toMutableList()
        val foodPrice = cartItemsList.mapNotNull { it.foodPrice }.toMutableList()
        val foodDescription = cartItemsList.mapNotNull { it.foodDescription }.toMutableList()
        val foodImage = cartItemsList.mapNotNull { it.foodImage }.toMutableList()
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()   // still works

        val restaurantId = cartItemsList.firstOrNull()?.restaurantId ?: ""

        orderNow(foodName, foodPrice, foodDescription, foodImage, foodQuantities, restaurantId)
    }

   private fun orderNow(
       foodName: MutableList<String>,
       foodPrice: MutableList<String>,
       foodDescription: MutableList<String>,
       foodImage: MutableList<String>,
       foodQuantities: MutableList<Int>,
       restaurantId: String
   ) {

       val totalAmount = calculateTotalAmount()

       val intent = Intent(requireContext(), PaymentMethodActivity::class.java).apply {

           putStringArrayListExtra("FoodItemName", ArrayList(foodName))
           putStringArrayListExtra("FoodItemPrice", ArrayList(foodPrice))
           putStringArrayListExtra("FoodItemImage", ArrayList(foodImage))
           putStringArrayListExtra("FoodItemDescription", ArrayList(foodDescription))
           putIntegerArrayListExtra("FoodItemQuantities", ArrayList(foodQuantities))

           putExtra("restaurantId", restaurantId)
           putExtra("totalAmount", totalAmount)
       }
       startActivity(intent)
   }
    private fun calculateTotalAmount(): String {

        var total = 0

        for (item in cartItemsList) {

            val price = item.foodPrice?.toIntOrNull() ?: 0
            val quantity = item.foodQuantity ?: 1

            total += price * quantity
        }

        return total.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}