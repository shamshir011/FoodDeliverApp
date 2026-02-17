package com.examples.waveoffood.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waveoffood.databinding.FoodCategoryBinding
import com.example.waveoffood.databinding.ViewAllLayoutDesignBinding
import com.examples.waveoffood.Model.CategoryItem
import com.examples.waveoffood.Model.FoodCategory
class FoodCategoryAdapter(
    private val categoryItems: List<CategoryItem>,
    private val onCategoryClick: (FoodCategory) -> Unit,
    private val onViewAllClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition = 0
    companion object {
        private const val TYPE_CATEGORY = 0
        private const val TYPE_VIEW_ALL = 1
    }

    override fun getItemCount(): Int = categoryItems.size

    fun setSelectedCategory(categoryName: String?) {

        val index = categoryItems.indexOfFirst {
            it is CategoryItem.Food &&
                    it.foodCategory.foodCategoryName == categoryName
        }

        if (index != -1 && index != selectedPosition) {

            val previousPosition = selectedPosition
            selectedPosition = index

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }




    override fun getItemViewType(position: Int): Int {
        return when (categoryItems[position]) {
            is CategoryItem.Food -> TYPE_CATEGORY
            is CategoryItem.ViewAll -> TYPE_VIEW_ALL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CATEGORY) {
            val binding = FoodCategoryBinding.inflate(LayoutInflater.from(parent.context), parent,false)
            CategoryViewHolder(binding)
        } else {
            val binding = ViewAllLayoutDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewAllViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = categoryItems[position]) {

            is CategoryItem.Food -> {
                (holder as CategoryViewHolder).bind(item.foodCategory)
            }

            is CategoryItem.ViewAll -> {
                (holder as ViewAllViewHolder).bind()
            }
        }
    }

    // ---------------- ViewHolders ----------------

    inner class CategoryViewHolder(
        private val binding: FoodCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

fun bind(category: FoodCategory) {

    binding.categoryName.text = category.foodCategoryName

    Glide.with(binding.root.context)
        .load(category.foodCategoryImage)
        .into(binding.categoryImage)

    if (adapterPosition == selectedPosition) {
        binding.selectedLine.visibility = View.VISIBLE
    } else {
        binding.selectedLine.visibility = View.GONE
    }

    binding.root.setOnClickListener {
        selectedPosition = adapterPosition
        notifyDataSetChanged()
        onCategoryClick(category)
    }
}

    }

    inner class ViewAllViewHolder(
        private val binding: ViewAllLayoutDesignBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.root.setOnClickListener {
                onViewAllClick()
            }
        }
    }
}
