package com.examples.waveoffood.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.BuyAgainItemBinding
import com.examples.waveoffood.Model.OrderItem

class OrderItemAdapter(
    private val orderItemList: List<OrderItem>,
    private val context: Context
) : RecyclerView.Adapter<OrderItemAdapter.BuyAgainViewHolder>() {

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