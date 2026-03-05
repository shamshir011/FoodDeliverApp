package com.examples.waveoffood

import android.R.attr.order
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
import com.examples.waveoffood.Model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class OrderTrackingActivity : AppCompatActivity() {
    private val binding: ActivityOrderTrackingBinding by lazy {
        ActivityOrderTrackingBinding.inflate(layoutInflater)
    }
    private lateinit var orderList: ArrayList<OrderDetails>
    private lateinit var adapter: HistoryAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth  = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        orderList = ArrayList()

        adapter = HistoryAdapter(orderList, this)
        binding.orderTrackingRecyclerViewId.layoutManager = LinearLayoutManager(this)
        binding.orderTrackingRecyclerViewId.adapter = adapter

        listenOrders()
    }
//        private fun listenOrders(){
//
//        val currentUserId = auth.currentUser?.uid ?: return
//
//        databaseReference.child("OrderDetails")
//            .addValueEventListener(object : ValueEventListener{
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    orderList.clear()
//                    for (orderSnapshot in snapshot.children) {
//                        val order = orderSnapshot.getValue(OrderDetails::class.java)
//                        if (order != null && order.userUid == currentUserId) {
//                            orderList.add(order)
//                            order.itemPushKey?.let { orderId ->
//                                order.restaurantId?.let { restId ->
//                                    listenForOrderUpdates(orderId, restId)
//                                }
//                            }
//                        }
//                    }
//                    val isEmpty = orderList.isEmpty()
//
//                    binding.emptyOrderLayout.root.visibility =
//                        if (isEmpty) View.VISIBLE else View.GONE
//
//                    binding.orderLayout.visibility =
//                        if (isEmpty) View.GONE else View.VISIBLE
//
//                    adapter.notifyDataSetChanged()
//                }
//                override fun onCancelled(error: DatabaseError) {}
//            })
//    }

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
                        orderList.clear()
                        orderList.add(latestOrder)

//                        Its showing orderId
                        binding.textViewOrderId.text = "#${latestOrder.itemPushKey?.takeLast(6)}"

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

//                        "Pending" -> {
//                            showPlacedUI()
//                        }
//                        "Accepted" -> {
//
//                            // 🔥 Confirmation Message
//                            binding.textViewConfirmation.text = "Your order has been accepted!"
//                            binding.textViewConfirmation.setTextColor(
//                                ContextCompat.getColor(this@OrderTrackingActivity, R.color.darkGreen)
//                            )
//
//                            binding.cardViewConfirmation.setCardBackgroundColor(
//                                ContextCompat.getColor(this@OrderTrackingActivity, R.color.lowGreen)
//                            )
//                            binding.viewAccepted.setBackgroundColor(
//                                ContextCompat.getColor(this@OrderTrackingActivity, R.color.darkGreen)
//                            )
//
//                            // 🔥 Change Accepted Step Icon
//                            binding.imageViewAccepted.setImageResource(R.drawable.full_check_circle_icon)
//                            binding.imageViewConfirmation.setImageResource(R.drawable.full_check_circle_icon)
//                        }

                        "Pending" -> updateTrackingUI(1)

                        "Accepted" -> updateTrackingUI(2)

                        "Preparing" -> updateTrackingUI(3)

                        "Out for Delivery" -> updateTrackingUI(4)

                        "Delivered" -> updateTrackingUI(5)

                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun showPlacedUI(){
        binding.imageViewOrderPlaced.setImageResource(R.drawable.full_check_circle_icon)
        binding.viewOrderPlaced.setBackgroundColor(
            ContextCompat.getColor(this@OrderTrackingActivity, R.color.darkGreen)
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
            binding.imageViewBeingPrepared.setImageResource(R.drawable.full_check_circle_icon)
            binding.viewBeingPrepared.setBackgroundColor(completedColor)
        }

        // Step 4 - Out For Delivery
        if (step >= 4) {
            binding.imageViewOrderPlaced.setImageResource(R.drawable.full_check_circle_icon)
            binding.imageViewOutForDelivery.setBackgroundColor(completedColor)
        }

        // Step 5 - Delivered
        if (step >= 5) {
            binding.imageViewDelivered.setImageResource(R.drawable.full_check_circle_icon)
        }
    }
}