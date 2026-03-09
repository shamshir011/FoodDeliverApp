package com.examples.waveoffood.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.waveoffood.databinding.FragmentHistoryBinding
import com.examples.waveoffood.Adapter.HistoryAdapter
import com.examples.waveoffood.Adapter.RestaurantRecommendedAdapter
import com.examples.waveoffood.Model.OrderDetails
import com.examples.waveoffood.Model.OrderItem
import com.examples.waveoffood.OrderTrackingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class HistoryFragment : Fragment(){
    private lateinit var orderBinding: FragmentHistoryBinding
    private lateinit var orderItemList: ArrayList<OrderItem>
    private lateinit var adapter: HistoryAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        orderBinding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        orderItemList = ArrayList()

        orderBinding.cardViewOrders.setOnClickListener {
            val intent = Intent(requireContext(), OrderTrackingActivity::class.java);
            startActivity(intent)
        }
        loadHistoryItem()

        return orderBinding.root
    }

    private fun loadHistoryItem(){

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        orderBinding.progressBar.visibility = View.VISIBLE

        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("user")
            .child(currentUserId)
            .child("BuyHistory")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                orderItemList.clear()

                for(foodSnapshot in snapshot.children){

                    val orderDetails = foodSnapshot.getValue(OrderDetails::class.java)

                    orderDetails?.let { order ->

                        val names = order.foodNames ?: emptyList()
                        val quantities = order.foodQuantities ?: emptyList()
                        val prices = order.foodPrices ?: emptyList()
                        val images = order.foodImages ?: emptyList()

                        for(i in names.indices){
                            val item = OrderItem(
                                foodName = names.getOrNull(i) ?: "",
                                quantity = quantities.getOrNull(i) ?: 0,
                                price = prices.getOrNull(i) ?: "",
                                image = images.getOrNull(i) ?: ""
                            )
                            orderItemList.add(item)
                        }
                    }
                }

                historyAdapter()
                orderBinding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun historyAdapter(){
        adapter = HistoryAdapter(orderItemList,requireContext())
        orderBinding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        orderBinding.recyclerViewHistory.adapter = adapter
    }
}
