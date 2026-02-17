package com.examples.waveoffood.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.BannerSlideItemBinding
import com.examples.waveoffood.Model.FoodItemModel

class BannerSliderAdapter(
    private val foodList: List<FoodItemModel>
) : RecyclerView.Adapter<BannerSliderAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val binding: BannerSlideItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder{
        val binding = BannerSlideItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {

        val item = foodList[position]

        holder.binding.textView38.text = item.title
        holder.binding.textView45.text = item.price

        Glide.with(holder.itemView.context)
            .load(item.itemImage)
            .into(holder.binding.imageView7)
    }

    override fun getItemCount(): Int = foodList.size
}