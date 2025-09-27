package com.examples.waveoffood.Fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.FragmentHistoryBinding
import com.examples.waveoffood.Adapter.BuyAgainAdapter
import com.examples.waveoffood.Model.OrderDetails
import com.examples.waveoffood.RecentOrderItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode
import java.util.ArrayList

class HistoryFragment : Fragment(){
    private lateinit var historyBinding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
//    private  var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        historyBinding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        //Initialize firebase auth
        auth = FirebaseAuth.getInstance()
        //Initialize firebase database
        database = FirebaseDatabase.getInstance()

        //Retrieve and display the user order history
        retrieveBuyHistory()

        historyBinding.recentBuyItem.setOnClickListener{
            seeItemsRecentBuy()
        }

        historyBinding.receivedButton.setOnClickListener{
            updateOrderStatus()
        }

        return historyBinding.root
    }

    private fun updateOrderStatus(){
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val completeOrderReference = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderReference.child("paymentReceived").setValue(true)
    }

    // Function to see items recent buy
    private fun seeItemsRecentBuy(){
        listOfOrderItem.firstOrNull()?.let { recentBuy->
            val intent = Intent(requireContext(), RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem", ArrayList(listOfOrderItem))
            startActivity(intent)
        }
    }

    // Function to retrieve items buy history
    private fun retrieveBuyHistory(){
        historyBinding.recentBuyItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""
        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")

        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                for (buySnapshot in snapshot.children){
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
                    // Display the most recent order details
                    setDataInRecentBuyItem()
                    // Setup to recyclerview with previous order details
                    setPreviousBuyItemsRecyclerView()
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    // Function to display the most recent order details
    private fun setDataInRecentBuyItem(){
        historyBinding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(historyBinding) {
                buyAgainFoodNameId.text = it.foodNames?.firstOrNull()?:""
                buyAgainFoodPriceId.text = it.foodPrices?.firstOrNull()?:""
                val image = it.foodImages?.firstOrNull()?:""
//                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(image).into(buyAgainImageId)

                val isOrderIsAccepted = listOfOrderItem[0].orderAccepted
//                Log.d("TAG", "setDataInRecentBuyItem: $isOrderIsAccepted")
                if(isOrderIsAccepted){
                    orderStatus.background.setTint(Color.GREEN)
                    receivedButton.visibility = View.VISIBLE
                }

//****************************************************************************************************

//                listOfOrderItem.reverse()
//                if (listOfOrderItem.isNotEmpty()){
//
//                }
//******************************************************************************************************
            }
        }
    }

    // Function to setup to recyclerview with previous order details
    private fun setPreviousBuyItemsRecyclerView(){
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()
        for (i in 0 until listOfOrderItem.size){
            listOfOrderItem[i].foodNames?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            listOfOrderItem[i].foodPrices?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let { buyAgainFoodImage.add(it) }
        }
        val rv = historyBinding.historyRecyclerViewId
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter = BuyAgainAdapter(
            buyAgainFoodName,
            buyAgainFoodPrice,
            buyAgainFoodImage,
            requireContext()
        )
        rv.adapter = buyAgainAdapter
    }
}
