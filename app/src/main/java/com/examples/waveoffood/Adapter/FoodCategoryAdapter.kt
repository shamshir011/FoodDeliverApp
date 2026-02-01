package com.examples.waveoffood.Adapter

import android.R.attr.category
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.R
import com.example.waveoffood.databinding.FoodCategoryBinding
import com.example.waveoffood.databinding.MenuItemBinding
import com.example.waveoffood.databinding.PopularItemBinding
import com.examples.waveoffood.DetailsActivity
import com.examples.waveoffood.Model.FoodCategory
import com.examples.waveoffood.Model.MenuItem

class FoodCategoryAdapter(
    private val categoryItems: List<FoodCategory>
//    private val requireContext: Context
)
    : RecyclerView.Adapter<FoodCategoryAdapter.CategoryViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder{
        val binding = FoodCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }



    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(position)

//        holder.itemView.setOnClickListener{
//            val intent = Intent(requireContext, DetailsActivity::class.java)
//            intent.putExtra("MenuItemName", item)
//            intent.putExtra("MenuItemImage", image)
//            requireContext.startActivity(intent)
//        }
    }

    override fun getItemCount(): Int{
        return categoryItems.size
    }

    inner class CategoryViewHolder(private val binding: FoodCategoryBinding): RecyclerView.ViewHolder(binding.root){
        // Adapter to another activity
        init{
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
//                    openDetailsActivity(position)
                    Toast.makeText(binding.root.context, "Category clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }
        fun bind(position: Int){
            val categoryItem  = categoryItems[position]
            binding.apply{
                categoryName.text = categoryItem.foodCategoryName
                val uri = Uri.parse(categoryItem.foodCategoryImage)
                Glide.with(binding.root.context).load(uri).into(categoryImage)

//                val imageUrl = categoryItem.foodCategoryImage
//                if (!imageUrl.isNullOrBlank()) {   // safe check
//                    val uri = Uri.parse(imageUrl)
//                    Glide.with(binding.root.context)
//                        .load(uri)
//                        .placeholder(R.drawable.ic_placeholder)   // ← add your placeholder drawable
//                        .error(R.drawable.ic_error)               // ← add error image
//                        .into(categoryImage)
//                } else {
//                    // No image → show placeholder or hide image view
//                    categoryImage.setImageResource(R.drawable.ic_placeholder)
//                    // or categoryImage.visibility = View.GONE
//                }
            }
        }
    }
}
