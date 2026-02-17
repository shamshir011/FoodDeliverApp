package com.examples.waveoffood.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.RecommendedRestaurantsBinding
import com.examples.waveoffood.Model.Restaurant
import com.google.firebase.database.DatabaseReference

class RestaurantRecommendedAdapter(
    private val context: Context,
    private val restaurantList: ArrayList<Restaurant>

): RecyclerView.Adapter<RestaurantRecommendedAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val binding = RecommendedRestaurantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.bind(position)
    }

    override fun getItemCount(): Int{
        return restaurantList.size
    }

    inner class ViewHolder(private val binding: RecommendedRestaurantsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val restaurantItem = restaurantList[position]
                val uriString = restaurantItem.restaurantImage
                val uri = Uri.parse(uriString)
                textViewRestaurant.text = restaurantItem.restaurantName
                Glide.with(context).load(uri).into(imageViewRestaurant)

            }
        }
    }


    fun updateList(newList: List<Restaurant>) {
        restaurantList.clear()
        restaurantList.addAll(newList)
        notifyDataSetChanged()
    }

}