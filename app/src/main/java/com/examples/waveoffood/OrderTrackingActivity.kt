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
        binding.orderTrackingRecyclerViewId.layoutManager =
            LinearLayoutManager(this)
        binding.orderTrackingRecyclerViewId.adapter = adapter

        listenOrders()

    }
        private fun listenOrders(){

        val currentUserId = auth.currentUser?.uid ?: return

        databaseReference.child("OrderDetails")
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    orderList.clear()

//                    for (orderSnapshot in snapshot.children){
//                        val order = orderSnapshot.getValue(OrderDetails::class.java)
//                        if (order != null && order.userUid == currentUserId) {
//                            orderList.add(order)
//                            listenForOrderUpdates(
//                                order.itemPushKey ?: continue,
//                                order.restaurantId ?: continue
//                            )
//                        }
//                    }
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetails::class.java)
                        if (order != null && order.userUid == currentUserId) {
                            orderList.add(order)
                            order.itemPushKey?.let { orderId ->
                                order.restaurantId?.let { restId ->
                                    listenForOrderUpdates(orderId, restId)
                                }
                            }
                        }
                    }
                    val isEmpty = orderList.isEmpty()

                    binding.emptyOrderLayout.root.visibility =
                        if (isEmpty) View.VISIBLE else View.GONE

                    binding.orderLayout.visibility =
                        if (isEmpty) View.GONE else View.VISIBLE

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

                        "Accepted" -> {

                            // 🔥 Confirmation Message
                            binding.textViewConfirmation.text = "Your order has been accepted!"
                            binding.textViewConfirmation.setTextColor(
                                ContextCompat.getColor(this@OrderTrackingActivity, R.color.darkGreen)
                            )

                            binding.cardViewConfirmation.setCardBackgroundColor(
                                ContextCompat.getColor(this@OrderTrackingActivity, R.color.lowGreen)
                            )
                            binding.viewOrderPlaced.setBackgroundColor(
                                ContextCompat.getColor(this@OrderTrackingActivity, R.color.darkGreen)
                            )

                            // 🔥 Change Accepted Step Icon
                            binding.imageViewAccepted.setImageResource(R.drawable.full_check_circle_icon)
                            binding.imageViewConfirmation.setImageResource(R.drawable.full_check_circle_icon)


                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}