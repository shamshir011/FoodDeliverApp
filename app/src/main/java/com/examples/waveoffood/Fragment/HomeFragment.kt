package com.examples.waveoffood.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.waveoffood.databinding.FragmentHomeBinding
import com.example.waveoffood.R
import com.examples.waveoffood.Adapter.BannerSliderAdapter
import com.examples.waveoffood.Adapter.FoodCategoryAdapter
import com.examples.waveoffood.Adapter.MenuAdapter
import com.examples.waveoffood.Adapter.RestaurantAdapter
import com.examples.waveoffood.Adapter.RestaurantRecommendedAdapter
import com.examples.waveoffood.Model.CategoryItem
import com.examples.waveoffood.Model.FoodCategory
import com.examples.waveoffood.Model.FoodItemModel
import com.examples.waveoffood.Model.MenuItem
import com.examples.waveoffood.Model.Restaurant
import com.examples.waveoffood.Model.RestaurantModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.os.Handler
import android.os.Looper


class HomeFragment : Fragment() {

    private lateinit var  databaseReference: DatabaseReference
    private lateinit var homeFragmentBiding: FragmentHomeBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>
    private lateinit var foodCategories: MutableList<FoodCategory>
    private var restaurantItems: ArrayList<Restaurant> = ArrayList()
    private lateinit var restaurantRecommendedAdapter: RestaurantRecommendedAdapter
    private val CATEGORY_LIMIT = 10
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var foodCategoryAdapter: FoodCategoryAdapter

    private val restaurantList = mutableListOf<RestaurantModel>()
    private val foodMap = mutableMapOf<String, List<FoodItemModel>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        foodCategories = mutableListOf()
        // Inflate the layout for this fragment
        homeFragmentBiding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        homeFragmentBiding.viewMenuId.setOnClickListener{
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }



        // Retrieve and display popular menu items
        retrieveFoodCategoryData()
        retrieveRestaurantItems()
        fetchRestaurants()

//        retrieveAndDisplayPopularItem()


        return homeFragmentBiding.root


    }

//    ********************  Retrieve food category data from database   ******************************

    private fun retrieveFoodCategoryData(){
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("FoodCategory")

        // Fetch data from data base
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foodCategories.clear()
                for (foodSnapshot in snapshot.children) {
                    val foodCategory = foodSnapshot.getValue(FoodCategory::class.java)
                    foodCategory?.let {
                        foodCategories.add(it)
                    }
                }
                homeFragmentBiding.categoryProgressBar.visibility = View.INVISIBLE
                homeFragmentBiding.recyclerViewCategory.visibility = View.VISIBLE

                setFoodCategoryAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }



//  *******************************  This function retrieving recommended for you data      *****************************
//    private fun retrieveRestaurantItems(){
//        database = FirebaseDatabase.getInstance()
//        val foodRef: DatabaseReference = database.reference.child("Restaurant")
//
//        // Fetch data from data base
//        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // Clear existing data before populating
//                restaurantItems.clear()
//
//                // loop for through each food item
//                for(foodSnapshot in snapshot.children){
//                    val categoryItem = foodSnapshot.getValue(Restaurant::class.java)
//                    categoryItem?.let {
//                        restaurantItems.add(it)
//                    }
//                    homeFragmentBiding.recommendedProgressBar.visibility = View.INVISIBLE
//                    homeFragmentBiding.recommendedRecyclerView.visibility = View.VISIBLE
//                }
//                restaurantSetAdapter()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("DatabaseError", "Error: ${error.message}")
//            }
//        })
//    }


//    Its modified data 23:31
//    This function retrieving recommended for you data
    private fun retrieveRestaurantItems() {

    database = FirebaseDatabase.getInstance()
    val foodRef: DatabaseReference = database.reference.child("Restaurant")

    foodRef.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {

            restaurantItems.clear()

            for (foodSnapshot in snapshot.children) {

                val categoryItem =
                    foodSnapshot.getValue(Restaurant::class.java)

                // 🔥 THIS LINE IS THE MOST IMPORTANT FIX
                categoryItem?.key = foodSnapshot.key

                categoryItem?.let {
                    restaurantItems.add(it)
                }
            }

            homeFragmentBiding.recommendedProgressBar.visibility = View.INVISIBLE
            homeFragmentBiding.recommendedRecyclerView.visibility = View.VISIBLE

            restaurantSetAdapter()
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("DatabaseError", "Error: ${error.message}")
        }
    })
}


//    This is for banner recyclerview
private fun fetchRestaurants() {
    val restaurantRef = FirebaseDatabase.getInstance()
        .getReference("Restaurant")
    restaurantRef.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot){

            restaurantList.clear()
            foodMap.clear()

            for (restaurantSnap in snapshot.children) {

                val restaurant =  restaurantSnap.getValue(RestaurantModel::class.java)

                restaurant?.key = restaurantSnap.key

                if (restaurant != null) {
                    restaurantList.add(restaurant)
                }
            }

            val count = restaurantList.size
            homeFragmentBiding.textViewCountRestaurant.text =
                if (count == 1) "$count"
                else "$count"

            fetchFoodItemsForAllRestaurants()
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

    private fun fetchFoodItemsForAllRestaurants() {

        val foodRef = FirebaseDatabase.getInstance()
            .getReference("foodItem")

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("CHECK_NODE", "Snapshot children count: ${snapshot.childrenCount}")

                for (restaurant in restaurantList) {

                    val restaurantId = restaurant.key ?: continue
                    val foodList = mutableListOf<FoodItemModel>()

                    val restaurantFoodSnap = snapshot.child(restaurantId)

                    for (itemSnap in restaurantFoodSnap.children) {

                        val foodItem =
                            itemSnap.getValue(FoodItemModel::class.java)

                        foodItem?.key = itemSnap.key

                        if (foodItem != null) {
                            foodList.add(foodItem)
                        }
                    }

                    foodMap[restaurantId] = foodList
                }

                setupRecyclerView()
            }


            override fun onCancelled(error: DatabaseError) {}
        })
    }


//  **************  Its for Filtering data(When i click on biryani then show only biryani) ********************

    private fun loadRestaurantsByCategory(category: String) {

        val ref = FirebaseDatabase.getInstance()
            .getReference("Restaurant")

        ref.orderByChild("restaurantCategories/$category")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val filteredBannerList = mutableListOf<RestaurantModel>()
                    val filteredRecommendedList = mutableListOf<Restaurant>()

                    for (data in snapshot.children) {

                        val bannerRestaurant =
                            data.getValue(RestaurantModel::class.java)
                        bannerRestaurant?.key = data.key
                        bannerRestaurant?.let { filteredBannerList.add(it) }

                        val recommendedRestaurant =
                            data.getValue(Restaurant::class.java)
                        recommendedRestaurant?.let {
                            filteredRecommendedList.add(it)
                        }
                    }

                    restaurantAdapter.updateList(filteredBannerList)
                    restaurantRecommendedAdapter.updateList(filteredRecommendedList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    //    This adapter for category place
private fun setFoodCategoryAdapter(){

    val uiList = mutableListOf<CategoryItem>()

    // Take only limited items
    val limitedList = if (foodCategories.size > CATEGORY_LIMIT) {
        foodCategories.take(CATEGORY_LIMIT)
    } else {
        foodCategories
    }
    // Convert to UI items
    limitedList.forEach {
        uiList.add(CategoryItem.Food(it))
    }

    // Add View All ONLY if there are more items
    if (foodCategories.size > CATEGORY_LIMIT) {
        uiList.add(CategoryItem.ViewAll)
    }

        foodCategoryAdapter = FoodCategoryAdapter(
            uiList,
            onCategoryClick = { category ->
                val categoryName = category.foodCategoryName ?: return@FoodCategoryAdapter

                if (categoryName == "All") {
                    fetchRestaurants()
                    retrieveRestaurantItems()
                } else {
                    loadRestaurantsByCategory(categoryName)
                }
            },
            onViewAllClick = {
                openBottomSheet()
            }
        )



        homeFragmentBiding.recyclerViewCategory.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    homeFragmentBiding.recyclerViewCategory.adapter = foodCategoryAdapter
    }

private fun restaurantSetAdapter() {
    restaurantRecommendedAdapter =
        RestaurantRecommendedAdapter(requireContext(), restaurantItems)
    val gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
    homeFragmentBiding.recommendedRecyclerView.layoutManager = gridLayoutManager
    homeFragmentBiding.recommendedRecyclerView.adapter = restaurantRecommendedAdapter
    }

//    Its modified code of the restaurant recommended for you
//    private fun restaurantSetAdapter() {
//
//        if (!::restaurantRecommendedAdapter.isInitialized) {
//
//            restaurantRecommendedAdapter =
//                RestaurantRecommendedAdapter(requireContext(), restaurantItems)
//
//            homeFragmentBiding.recommendedRecyclerView.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//
//            homeFragmentBiding.recommendedRecyclerView.adapter =
//                restaurantRecommendedAdapter
//
//        } else {
//            restaurantRecommendedAdapter.notifyDataSetChanged()
//        }
//    }

    private fun setupRecyclerView() {

        restaurantAdapter = RestaurantAdapter(restaurantList, foodMap)

        homeFragmentBiding.bannerRecyclerviewId.layoutManager = LinearLayoutManager(requireContext())
        homeFragmentBiding.bannerRecyclerviewId.adapter = restaurantAdapter
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageSlider = homeFragmentBiding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object: ItemClickListener{
            override fun doubleClick(position: Int) {

            }

            override fun onItemSelected(position: Int){
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun highlightSelectedCategory(categoryName: String?) {
        foodCategoryAdapter.setSelectedCategory(categoryName)
    }

    private fun showSelectedCategoryInHome(categoryName: String) {

        val selectedItem = foodCategories.find {
            it.foodCategoryName == categoryName
        } ?: return

        val newList = mutableListOf<CategoryItem>()



        foodCategories
            .filter { it.foodCategoryName != categoryName }
            .take(CATEGORY_LIMIT - 1)
            .forEach {
                newList.add(CategoryItem.Food(it))
            }

        newList.add(CategoryItem.Food(selectedItem))

        newList.add(CategoryItem.ViewAll)

        foodCategoryAdapter = FoodCategoryAdapter(
            newList,
            onCategoryClick = { category ->
                val name = category.foodCategoryName ?: return@FoodCategoryAdapter
                highlightSelectedCategory(name)

                if (name == "All") {
                    fetchRestaurants()
                    retrieveRestaurantItems()
                } else {
                    loadRestaurantsByCategory(name)
                }
            },
            onViewAllClick = {
                openBottomSheet()
            }
        )

        homeFragmentBiding.recyclerViewCategory.adapter = foodCategoryAdapter


        // Highlight
        highlightSelectedCategory(categoryName)
    }


//    Now added
    private fun openBottomSheet() {

        val bottomSheet = CuisineDishesBottomSheetFragment()

        bottomSheet.categoryClickListener = { selectedCategory ->
            selectedCategory.foodCategoryName?.let { categoryName ->
                showSelectedCategoryInHome(categoryName)

                if (categoryName == "All") {
                    fetchRestaurants()
                    retrieveRestaurantItems()
                } else {
                    loadRestaurantsByCategory(categoryName)
                }
            }
        }

        bottomSheet.show(parentFragmentManager, "BottomSheet")
    }

    override fun onStart() {
        super.onStart()

    }
}