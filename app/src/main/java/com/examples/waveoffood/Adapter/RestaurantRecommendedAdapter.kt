package com.examples.waveoffood.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.RecommendedRestaurantsBinding
import com.examples.waveoffood.Model.Restaurant
import com.examples.waveoffood.RestaurantItemActivity
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
//        return restaurantList.size
        return if (restaurantList.size > 10) 10 else restaurantList.size
    }

    inner class ViewHolder(private val binding: RecommendedRestaurantsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val restaurantItem = restaurantList[position]
            binding.apply {

                val uriString = restaurantItem.restaurantImage
                val uri = Uri.parse(uriString)
                textViewRestaurant.text = restaurantItem.restaurantName
                Glide.with(context).load(uri).into(imageViewRestaurant)
            }

//            binding.root.setOnClickListener {
//                val intent = Intent(context, RestaurantItemActivity::class.java)
//                intent.putExtra("restaurantId", restaurantItem.key)
//                context.startActivity(intent)
//            }

            binding.root.setOnClickListener {

                Log.d("CLICK_DEBUG", "Clicked Key = ${restaurantItem.key}")

                val intent = Intent(context, RestaurantItemActivity::class.java)
                intent.putExtra("restaurantId", restaurantItem.key)
                context.startActivity(intent)
            }

        }
    }
    fun updateList(newList: List<Restaurant>) {
        restaurantList.clear()
        restaurantList.addAll(newList)
        notifyDataSetChanged()
    }
}