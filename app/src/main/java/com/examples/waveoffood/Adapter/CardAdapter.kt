package com.examples.waveoffood.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CardAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private var cartDescriptions:MutableList<String>,
    private val cartFoodImages: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
    private val cartIngredient: MutableList<String>)
    : RecyclerView.Adapter<CardAdapter.CartViewHolder>(){
        //instance firebase
        private val auth = FirebaseAuth.getInstance()

    init {
        //Initialize firebase
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid?:""
        val cartItemNumber = cartItems.size

        itemQuantities = IntArray(cartItemNumber){1}
        cartItemsReference = database.reference.child("user").child(userId).child("CartItems")
    }

    companion object{
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapter.CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardAdapter.CartViewHolder, position: Int){

        holder.bind(position)
    }

    override fun getItemCount(): Int{
        return cartItems.size
    }
    //Get updated quantity
    fun getUpdatedItemsQuantities(): MutableList<Int>{
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding:CartItemBinding): RecyclerView.ViewHolder(binding.root){
        private var itemQuantities = IntArray(cartItems.size){1}
        fun bind(position: Int){
            binding.apply {
                val quantity = itemQuantities[position]
                cardFoodName.text = cartItems[position]
                cartItemPrice.text = cartItemPrices[position]
                //Load image using Glide
                val uriString = cartFoodImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartImage)

                cartItemQuantity.text = quantity.toString()

                binding.minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }

                binding.Plusbutton.setOnClickListener {
                    increaseQuantity(position)
                }

                binding.deleteButton.setOnClickListener{

                    val itemPosition =  adapterPosition
                    if(itemPosition != RecyclerView.NO_POSITION){
//                        deleteItem(position)
                        deleteItem(itemPosition)
                    }
                }
            }
        }
        private fun decreaseQuantity(position: Int){
            if(itemQuantities[position] > 1){
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int){
            if (itemQuantities[position] < 10){
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        fun deleteItem(position: Int){
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve){ uniqueKey ->
                if(uniqueKey != null){
                    removeItem(position, uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if(uniqueKey != null){
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {

//                    cartItems.removeAt(position)
                    cartFoodImages.removeAt(position)
                    cartDescriptions.removeAt(position)
                    cartQuantity.removeAt(position)
                    cartItemPrices.removeAt(position)
                    cartIngredient.removeAt(position)
                    Toast.makeText(context,"Item Delete", Toast.LENGTH_SHORT).show()

                    // update itemQuantities
                    itemQuantities = itemQuantities.filterIndexed { index, i -> index != position}.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context,"Failed to Delete", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete:(String?)-> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey:String? = null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if(index == positionRetrieve){
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError){

                }
            })
        }

    }
}