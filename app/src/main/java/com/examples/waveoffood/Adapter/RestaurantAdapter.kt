package com.examples.waveoffood.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.waveoffood.databinding.RestaurantItemBinding
import com.examples.waveoffood.Model.FoodItemModel
import com.examples.waveoffood.Model.RestaurantModel
import android.os.Handler
import android.os.Looper
import com.examples.waveoffood.RestaurantItemActivity

class RestaurantAdapter(
    private val restaurantList: MutableList<RestaurantModel>,
    private val foodMap: Map<String, List<FoodItemModel>>
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(val binding: RestaurantItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = RestaurantItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {


//        This code for banner automatic loop


        val restaurant = restaurantList[position]

        holder.binding.textViewRestaurantName.text = restaurant.restaurantName
        holder.binding.textView42.text = restaurant.restaurantDeliveryDuration
        holder.binding.textView44.text = restaurant.restaurantDeliveryRadius


//    *************************************    code for  viewpager      *****************************
        val foodList = foodMap[restaurant.key] ?: emptyList()

        val bannerAdapter = BannerSliderAdapter(foodList)

        holder.binding.bannerViewPager.currentItem = 0
        holder.binding.bannerViewPager.adapter = bannerAdapter

        holder.binding.dotsIndicator.attachTo(holder.binding.bannerViewPager)

        if (foodList.isNotEmpty()) {
            holder.binding.bannerViewPager.postDelayed(object : Runnable {
                override fun run() {

                    val nextItem = holder.binding.bannerViewPager.currentItem + 1

                    if (nextItem < foodList.size) {
                        holder.binding.bannerViewPager.currentItem = nextItem
                    } else {
                        holder.binding.bannerViewPager.currentItem = 0
                    }

                    holder.binding.bannerViewPager.postDelayed(this, 3000)
                }
            }, 3000)
        }
        holder.binding.root.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RestaurantItemActivity::class.java)
            intent.putExtra("restaurantId", restaurant.key)
            intent.putExtra("restaurantName",restaurant.restaurantName)
            intent.putExtra("restaurantDistance",restaurant.restaurantDeliveryRadius)
            intent.putExtra("restaurantDeliveryDuration",restaurant.restaurantDeliveryDuration)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = restaurantList.size

    fun updateList(newList: List<RestaurantModel>) {
        restaurantList.clear()
        restaurantList.addAll(newList)
        notifyDataSetChanged()
    }
}
