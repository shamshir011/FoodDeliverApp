package com.examples.waveoffood.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.CuisineDishesLayoutDesignBinding
import com.example.waveoffood.databinding.FoodCategoryBinding
import com.examples.waveoffood.Model.FoodCategory

class CuisinesDishesAdapter(
    private val cuisineDishes: List<FoodCategory>,
    private val requireContext: Context)
    : RecyclerView.Adapter<CuisinesDishesAdapter.CuisineDishesViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineDishesViewHolder {
        val binding = CuisineDishesLayoutDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuisineDishesViewHolder(binding)
    }



    override fun onBindViewHolder(holder: CuisineDishesViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cuisineDishes.size

    inner class CuisineDishesViewHolder(private val binding: CuisineDishesLayoutDesignBinding): RecyclerView.ViewHolder(binding.root){

        // Adapter to another activity
        init{
            binding.root.setOnClickListener{

                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
//                    openDetailsActivity(position)
                    Toast.makeText(requireContext, "This is the bottom sheet", Toast.LENGTH_SHORT).show()
                }
            }
        }



        // Set data in to recyclerview items, price, image
        fun bind(position: Int){
            val cuisineDish  = cuisineDishes[position]
            binding.apply{
                textViewCuisineDish.text = cuisineDish.foodCategoryName
                val uri = Uri.parse(cuisineDish.foodCategoryImage)
                Glide.with(requireContext).load(uri).into(imageViewCuisineDish)
            }
        }
    }
}



//        private fun openDetailsActivity(position: Int) {
//            val menuItems = cuisineDishes[position]
//            // A intent to open details activity and pass the data
//            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
//                putExtra("MenuItemName", menuItems.foodName)
//                putExtra("MenuItemImage", menuItems.foodImage)
//                putExtra("MenuItemDescription", menuItems.foodDescription)
//                putExtra("MenuItemIngredients", menuItems.foodIngredient)
//                putExtra("MenuItemPrice", menuItems.foodPrice)
//            }
//            // Start the details activity
//            requireContext.startActivity(intent)
//        }