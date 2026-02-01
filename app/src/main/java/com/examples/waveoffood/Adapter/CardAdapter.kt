package com.examples.waveoffood.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.CartItemBinding
import com.examples.waveoffood.Model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//This code from Grok ai

class CardAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItems>   // ← only this list
) : RecyclerView.Adapter<CardAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var cartItemsReference: DatabaseReference

    private var itemQuantities: IntArray = intArrayOf()

    init {
        val userId = auth.currentUser?.uid ?: ""
        cartItemsReference = FirebaseDatabase.getInstance()
            .reference
            .child("user")
            .child(userId)
            .child("CartItems")

        updateQuantities()
    }

    private fun updateQuantities() {
        itemQuantities = IntArray(cartItems.size) { i ->
            cartItems[i].foodQuantity ?: 1
        }
    }

    // Call this after delete or any list change
    fun refreshAfterChange() {
        updateQuantities()
        notifyDataSetChanged()   // or more precise notifies if you want
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount() = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position], position)
    }

    fun getUpdatedItemsQuantities(): MutableList<Int> =
        cartItems.map { it.foodQuantity ?: 1 }.toMutableList()

    inner class CartViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItems, position: Int) {
            binding.apply {
                cardFoodName.text = item.foodName ?: ""
                cartItemPrice.text = item.foodPrice ?: ""
                cartItemQuantity.text = (item.foodQuantity ?: 1).toString()

                item.foodImage?.let {
                    Glide.with(context).load(Uri.parse(it)).into(cartImage)
                }

                minusButton.setOnClickListener { decreaseQuantity(position) }
                Plusbutton.setOnClickListener { increaseQuantity(position) }
                deleteButton.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        deleteItem(adapterPosition)
                    }
                }
            }
        }

        private fun decreaseQuantity(position: Int) {
            val item = cartItems.getOrNull(position) ?: return
            if ((item.foodQuantity ?: 1) > 1) {
                item.foodQuantity = (item.foodQuantity ?: 1) - 1
                binding.cartItemQuantity.text = item.foodQuantity.toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            val item = cartItems.getOrNull(position) ?: return
            if ((item.foodQuantity ?: 1) < 10) {
                item.foodQuantity = (item.foodQuantity ?: 1) + 1
                binding.cartItemQuantity.text = item.foodQuantity.toString()
            }
        }

        private fun deleteItem(position: Int) {
            val item = cartItems.getOrNull(position) ?: return
            val key = item.key ?: return

            cartItemsReference.child(key).removeValue()
                .addOnSuccessListener {
                    cartItems.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                    updateQuantities()
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}