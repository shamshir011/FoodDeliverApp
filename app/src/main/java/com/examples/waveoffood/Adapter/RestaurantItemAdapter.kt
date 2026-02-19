package com.examples.waveoffood.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.RestaurantItemDesignLayoutBinding
import com.examples.waveoffood.Fragment.AddToCartBottomSheetFragment
import com.examples.waveoffood.Model.FoodItemModel

class RestaurantItemAdapter(
    private val context: Context,
    private val restaurantItemList: ArrayList<FoodItemModel>

): RecyclerView.Adapter<RestaurantItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val binding = RestaurantItemDesignLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.bind(position)
    }

    override fun getItemCount(): Int{
        return restaurantItemList.size
    }

    inner class ViewHolder(private val binding: RestaurantItemDesignLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val restaurantItem = restaurantItemList[position]
                val uriString = restaurantItem.itemImage
                val uri = Uri.parse(uriString)
                textViewItemName.text = restaurantItem.title
                textViewPrice.text = restaurantItem.price
                textViewDescription.text = restaurantItem.description
                Glide.with(context).load(uri).into(imageViewItemImage)
            }

            binding.root.setOnClickListener {

                val restaurantItem = restaurantItemList[position]

                Log.d("CLICK_DEBUG", "Clicked Item = ${restaurantItem.title}")

                val bottomSheet = AddToCartBottomSheetFragment.newInstance(restaurantItem)

                bottomSheet.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "AddToCartBottomSheetFragment"
                )
            }
        }
    }
}