package com.examples.waveoffood

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.waveoffood.R
import com.example.waveoffood.databinding.ActivityRestaurantItemBinding
import com.examples.waveoffood.Adapter.RestaurantItemAdapter
import com.examples.waveoffood.Model.FoodItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RestaurantItemActivity : AppCompatActivity() {
    private val binding: ActivityRestaurantItemBinding by lazy {
        ActivityRestaurantItemBinding.inflate(layoutInflater)
    }
    private lateinit var restaurantId: String
    private lateinit var restaurantItemAdapter: RestaurantItemAdapter
    private lateinit var database: FirebaseDatabase
    private val foodItemList = ArrayList<FoodItemModel>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backNavigation.setOnClickListener{
            finish()
        }





        restaurantId = intent.getStringExtra("restaurantId") ?: ""
        binding.textViewRadius.text = intent.getStringExtra("restaurantDistance")
        binding.textViewDuration.text = intent.getStringExtra("restaurantDeliveryDuration")
        binding.textViewName.text = intent.getStringExtra("restaurantName")
        Log.d("CHECK_ID", "RestaurantId from intent = $restaurantId")

        loadFoodItems()

    }
    private fun loadFoodItems() {

        binding.progressBar.visibility = View.VISIBLE

        val databaseRef = FirebaseDatabase.getInstance().getReference("foodItem")

        databaseRef.child(restaurantId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("CHECK_DATA", "Snapshot exists: ${snapshot.exists()}")

                    foodItemList.clear()

                    for (foodSnapshot in snapshot.children) {
                        Log.d("CHECK_DATA", "Food item: ${foodSnapshot.value}")
                        val item = foodSnapshot.getValue(FoodItemModel::class.java)
                        item?.let {
                            foodItemList.add(it)
                        }
                    }
                    binding.progressBar.visibility = View.GONE

                    Log.d("CHECK_DATA", "List size: ${foodItemList.size}")
                    setAdapter()
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Log.d("CHECK_DATA", "Error: ${error.message}")
                }
            })
    }

    private fun setAdapter() {
        restaurantItemAdapter =
            RestaurantItemAdapter(this, foodItemList)
        binding.restaurantFoodItemRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.restaurantFoodItemRecyclerview.adapter = restaurantItemAdapter

    }

}