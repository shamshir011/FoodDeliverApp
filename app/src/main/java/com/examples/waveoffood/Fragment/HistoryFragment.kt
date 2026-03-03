package com.examples.waveoffood.Fragment

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class HistoryFragment : Fragment(){
    private lateinit var orderBinding: FragmentHistoryBinding
    private lateinit var orderList: ArrayList<OrderDetails>
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

        orderList = ArrayList()

//        adapter = HistoryAdapter(orderList, requireContext())
//
//        orderBinding.orderRecyclerViewId.layoutManager =
//            LinearLayoutManager(requireContext())
//        orderBinding.orderRecyclerViewId.adapter = adapter
//
//        listenOrders()
//        listenForOrderUpdates()
        loadHistoryItem()

        return orderBinding.root
    }

    private fun loadHistoryItem(){
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        orderBinding.progressBar.visibility = View.VISIBLE
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("OrderDetails")

        databaseRef.orderByChild("userUid")
            .equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    orderList.clear()

                    for(foodSnapshot in snapshot.children){
                        val categoryItem = foodSnapshot.getValue(OrderDetails::class.java)
                        categoryItem?.let {
                            orderList.add(it)
                        }
                    }
                    historyAdapter()
                    orderBinding.progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun historyAdapter(){
        adapter = HistoryAdapter(orderList,requireContext())
        orderBinding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        orderBinding.recyclerViewHistory.adapter = adapter
    }

//    private fun listenOrders() {
//
//        val currentUserId = auth.currentUser?.uid ?: return
//
//        databaseReference.child("OrderDetails")
//            .addValueEventListener(object : ValueEventListener {
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    orderList.clear()
//
//                    for (orderSnapshot in snapshot.children) {
//                        val order =
//                            orderSnapshot.getValue(OrderDetails::class.java)
//
//                        if (order != null && order.userUid == currentUserId) {
//                            orderList.add(order)
//                        }
//                    }
//
//                    val isEmpty = orderList.isEmpty()
//
//                    orderBinding.emptyOrderLayout.root.visibility =
//                        if (isEmpty) View.VISIBLE else View.GONE
//
//                    orderBinding.orderLayout.visibility =
//                        if (isEmpty) View.GONE else View.VISIBLE
//
//                    adapter.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//            })
//    }


//    private fun listenForOrderUpdates(orderId: String, restaurantId: String) {
//
//        FirebaseDatabase.getInstance().reference
//            .child("restaurantOrders")
//            .child(restaurantId)
//            .child(orderId)
//            .addValueEventListener(object : ValueEventListener {
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    val order = snapshot.getValue(OrderDetails::class.java)
//
//                    if (order != null && order.orderAccepted == true) {
//                        orderBinding.textViewConfirmation.text = "Your order has been accepted!"
//                        orderBinding.textViewConfirmation.setTextColor(Color.GREEN)
//                        orderBinding.cardViewConfirmation.setBackgroundColor(
//                            ContextCompat.getColor(requireContext(), R.color.lowGreen)
//                        )
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//            })
//    }

}
