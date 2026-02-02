package com.examples.waveoffood.Model

sealed class CategoryItem {
    data class Food(val foodCategory: FoodCategory) : CategoryItem()
    object ViewAll : CategoryItem()
}
