package com.examples.waveoffood.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.MenuItemBinding
import com.examples.waveoffood.DetailsActivity
import com.examples.waveoffood.Model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context)
    : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root){

        // Adapter to another activity
        init{
            binding.root.setOnClickListener{

                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItems = menuItems[position]
            // A intent to open details activity and pass the data
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItems.foodName)
                putExtra("MenuItemImage", menuItems.foodImage)
                putExtra("MenuItemDescription", menuItems.foodDescription)
                putExtra("MenuItemIngredients", menuItems.foodIngredient)
                putExtra("MenuItemPrice", menuItems.foodPrice)
            }
            // Start the details activity
            requireContext.startActivity(intent)
        }

        // Set data in to recyclerview items, price, image
        fun bind(position: Int){
            val menuItem  = menuItems[position]
            binding.apply{
                menuFoodName.text = menuItem.foodName
                menuPrice.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)
            }
        }
    }
}

//progressBar.visibility = android.view.View.GONE
