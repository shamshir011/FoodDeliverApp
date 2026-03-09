package com.examples.waveoffood.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.BuyAgainItemBinding
import com.examples.waveoffood.Model.OrderDetails
import com.examples.waveoffood.Model.OrderItem


//class HistoryAdapter(
//    private val orderList: List<OrderDetails>,
//    private val context: Context
//) : RecyclerView.Adapter<HistoryAdapter.BuyAgainViewHolder>(){
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
//        val binding =
//            BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return BuyAgainViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
//        holder.bind(orderList[position])
//    }
//
//    override fun getItemCount(): Int{
//        return orderList.size
//    }
//
//    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(order: OrderDetails) {
//
//            binding.textViewFoodName.text = order.foodNames?.get(0)
//            val quantity = order.foodQuantities?.firstOrNull() ?: 0
//            binding.textViewQuantity.text = quantity.toString()
//            binding.textViewItemPrice.text = order.foodPrices?.get(0)
//
//            val imageUri = order.foodImages?.get(0)
//
//            Glide.with(context)
//                .load(imageUri)
//                .into(binding.imageViewOrderFoodImage)
//        }
//    }
//
//}


class HistoryAdapter(
    private val orderItemList: List<OrderItem>,
    private val context: Context
) : RecyclerView.Adapter<HistoryAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(orderItemList[position])
    }

    override fun getItemCount(): Int {
        return orderItemList.size
    }

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {

            binding.textViewFoodName.text = item.foodName
            binding.textViewQuantity.text = item.quantity.toString()
            binding.textViewItemPrice.text = item.price

            Glide.with(context)
                .load(item.image)
                .into(binding.imageViewOrderFoodImage)
        }
    }
}