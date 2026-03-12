package com.examples.waveoffood

import android.R.attr.order
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.waveoffood.R
import com.example.waveoffood.databinding.ActivityOrderTrackingBinding
import com.examples.waveoffood.Adapter.HistoryAdapter
import com.examples.waveoffood.Adapter.OrderItemAdapter
import com.examples.waveoffood.Fragment.HistoryFragment
import com.examples.waveoffood.Model.OrderDetails
import com.examples.waveoffood.Model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class OrderTrackingActivity : AppCompatActivity() {
    private val binding: ActivityOrderTrackingBinding by lazy{
        ActivityOrderTrackingBinding.inflate(layoutInflater)
    }
    private lateinit var orderList: ArrayList<OrderDetails>
    private val orderItemList = mutableListOf<OrderItem>()
    private lateinit var adapter: OrderItemAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth  = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        orderList = ArrayList()

        adapter = OrderItemAdapter(orderItemList, this)
        binding.orderTrackingRecyclerViewId.layoutManager = LinearLayoutManager(this)
        binding.orderTrackingRecyclerViewId.adapter = adapter
        binding.progressBarOrderTracking.visibility = View.VISIBLE
        listenOrders()

        binding.imageViewBackNavigation.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun listenOrders() {

        val currentUserId = auth.currentUser?.uid ?: return

        databaseReference.child("OrderDetails")
            .orderByChild("userUid")
            .equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    var latestOrder: OrderDetails? = null

                    for (orderSnapshot in snapshot.children) {

                        val order = orderSnapshot.getValue(OrderDetails::class.java)

                        if (order != null) {
                            if (latestOrder == null ||
                                order.currentTime > latestOrder.currentTime) {
                                latestOrder = order
                            }
                        }
                    }

                    if (latestOrder != null) {

                        orderItemList.clear()

                        val names = latestOrder.foodNames ?: emptyList()
                        val quantities = latestOrder.foodQuantities ?: emptyList()
                        val prices = latestOrder.foodPrices ?: emptyList()
                        val images = latestOrder.foodImages ?: emptyList()

                        for (i in names.indices) {

                            val item = OrderItem(
                                foodName = names.getOrNull(i) ?: "",
                                quantity = quantities.getOrNull(i) ?: 0,
                                price = prices.getOrNull(i) ?: "",
                                image = images.getOrNull(i) ?: ""
                            )

                            orderItemList.add(item)
                        }

                        binding.textViewOrderId.text =
                            "#${latestOrder.itemPushKey?.takeLast(6)}"

                        latestOrder.itemPushKey?.let { orderId ->
                            latestOrder.restaurantId?.let { restId ->
                                listenForOrderUpdates(orderId, restId)
                            }
                        }

                        binding.emptyOrderLayout.root.visibility = View.GONE
                        binding.orderLayout.visibility = View.VISIBLE

                    } else {

                        binding.emptyOrderLayout.root.visibility = View.VISIBLE
                        binding.orderLayout.visibility = View.GONE
                    }

                    adapter.notifyDataSetChanged()
                    binding.progressBarOrderTracking.visibility = View.GONE
                }


                override fun onCancelled(error: DatabaseError) {}
            })
    }
    private fun listenForOrderUpdates(orderId: String, restaurantId: String) {

        FirebaseDatabase.getInstance().reference
            .child("restaurantOrders")
            .child(restaurantId)
            .child(orderId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val order = snapshot.getValue(OrderDetails::class.java)
                    if (order == null) return

                    when (order.status) {

                        "Pending" -> updateTrackingUI(1)

                        "Accepted" -> updateTrackingUI(2)

                        "Preparing" -> updateTrackingUI(3)

                        "Out for Delivery" -> updateTrackingUI(4)

                        "Delivered" -> updateTrackingUI(5)
                        "Rejected" -> showRejectedUI()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
private fun showRejectedUI() {
    binding.textViewConfirmation.text = "Sorry, the restaurant rejected your order"
    binding.textViewConfirmation.setTextColor(
            ContextCompat.getColor(this@OrderTrackingActivity, R.color.crimsonRed)
        )
    binding.imageViewConfirmation.setImageResource(R.drawable.dismiss_button_icon)
    binding.imageViewAccepted.setImageResource(R.drawable.dismiss_button_icon)
    binding.viewAccepted.setBackgroundColor(
        ContextCompat.getColor(this@OrderTrackingActivity, R.color.crimsonRed)
    )
}

//    This function control order tracking UI
    private fun updateTrackingUI(step: Int) {
        val completedColor = ContextCompat.getColor(this, R.color.darkGreen)
//        val defaultColor = ContextCompat.getColor(this, R.color.gray)

        // Step 1 - Order Placed
        if (step >= 1) {
            binding.imageViewOrderPlaced.setImageResource(R.drawable.full_check_circle_icon)
            binding.viewOrderPlaced.setBackgroundColor(completedColor)
        }

        // Step 2 - Accepted
        if (step >= 2) {
            binding.imageViewAccepted.setImageResource(R.drawable.full_check_circle_icon)
            binding.imageViewConfirmation.setImageResource(R.drawable.full_check_circle_icon)
            binding.viewAccepted.setBackgroundColor(completedColor)
            binding.textViewConfirmation.text = "Your order has been accepted!"

            binding.textViewConfirmation.setTextColor(
            ContextCompat.getColor(this@OrderTrackingActivity, R.color.darkGreen)
            )
            binding.cardViewConfirmation.setBackgroundColor(
                ContextCompat.getColor(this@OrderTrackingActivity, R.color.lowGreen)
            )
        }

        // Step 3 - Preparing
        if (step >= 3) {
//            binding.imageViewBeingPrepared.setImageResource(R.drawable.full_check_circle_icon)
//            binding.viewBeingPrepared.setBackgroundColor(completedColor)
        }

        // Step 4 - Out For Delivery
        if (step >= 4) {
            binding.imageViewOrderPlaced.setImageResource(R.drawable.full_check_circle_icon)
//            binding.imageViewOutForDelivery.setBackgroundColor(completedColor)
        }

        // Step 5 - Delivered
        if (step >= 5) {
            binding.imageViewBeingPrepared.setImageResource(R.drawable.full_check_circle_icon)
            binding.viewBeingPrepared.setBackgroundColor(completedColor)
            binding.imageViewOutForDelivery.setImageResource(R.drawable.full_check_circle_icon)
            binding.viewOutForDelivery.setBackgroundColor(completedColor)
            binding.imageViewDelivered.setImageResource(R.drawable.full_check_circle_icon)
            binding.textViewConfirmation.setText("🎉Your order has been delivered🎉")

        }
    }
}