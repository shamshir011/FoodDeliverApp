package com.examples.waveoffood

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.ActivityDetailsBinding
import com.examples.waveoffood.Model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {

    private val binding: ActivityDetailsBinding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredients: String? = null
    private var foodPrice: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodImage = intent.getStringExtra("MenuItemImage")

        with(binding) {
            detailsFoodName.text = foodName
            textViewDescription.text = foodDescription
            textViewIngredients.text = foodIngredients
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage))
                .into(imageViewDetailFoodImage)
        }

        binding.imageButton.setOnClickListener {
            finish()
        }

        binding.addItemButton.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid?:""

        //Create a cartItems object
        val cartItems = CartItems(foodName.toString(), foodPrice.toString(), foodDescription.toString(), foodImage.toString(),1)

        //Save data to cart item to firebase database
        database.child("user").child(userId).child("CartItems").push().setValue(cartItems)
            .addOnSuccessListener {
            Toast.makeText(this, "Items added into cart successfully😆", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Item not added😓", Toast.LENGTH_SHORT).show()
        }
    }
}