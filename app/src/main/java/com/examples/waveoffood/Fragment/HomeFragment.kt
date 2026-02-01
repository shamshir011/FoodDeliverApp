package com.examples.waveoffood.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.waveoffood.databinding.FragmentHomeBinding
import com.examples.waveoffood.Adapter.PopularAdapter
import com.example.waveoffood.R
import com.examples.waveoffood.Adapter.FoodCategoryAdapter
import com.examples.waveoffood.Adapter.MenuAdapter
import com.examples.waveoffood.Model.FoodCategory
import com.examples.waveoffood.Model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var homeFragmentBiding: FragmentHomeBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>
    private lateinit var foodCategories: MutableList<FoodCategory>

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
        retrieveAndDisplayPopularItem()


        return homeFragmentBiding.root


    }

    private fun retrieveAndDisplayPopularItem() {
        //Get reference to the database
        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child("menu")
        menuItems = mutableListOf()

//        retrieve item from the database
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                for(foodSnapShot in snapshot.children){
                    val menuItem = foodSnapShot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                        homeFragmentBiding.recommendedProgressBar.visibility = View.INVISIBLE
                        homeFragmentBiding.popularRecyclerView.visibility = View.VISIBLE
                    }
                }
                //Display a random popular item
                randomPopularItem()
            }
            override fun onCancelled(error: DatabaseError){

            }
        })
    }
    private fun randomPopularItem() {
        //Create as shuffled list of menu items
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subsetMenuItems = index.take(numItemToShow).map { menuItems[it] }

        setPopularItemsAdapter(subsetMenuItems)
    }

    private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItem>){
        val adapter = MenuAdapter(subsetMenuItems,requireContext())
        homeFragmentBiding.popularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeFragmentBiding.popularRecyclerView.adapter = adapter
    }

//    ********************  Retrieve food category data from database   ******************************

    private fun retrieveFoodCategoryData(){
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("FoodCategory")

        // Fetch data from data base
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing data before populating
                foodCategories.clear()

                // loop for through each food item
                for(foodSnapshot in snapshot.children){
                    val foodCategory = foodSnapshot.getValue(FoodCategory::class.java)
                    foodCategory?.let {
                        foodCategories.add(it)
                        homeFragmentBiding.categoryProgressBar.visibility = View.INVISIBLE
                        homeFragmentBiding.recyclerViewCategory.visibility = View.VISIBLE
                    }
                }
//*******************************     Showing limit data to recyclerview   **********************************
//                categoryItem()
                setFoodCategoryAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }

//    private fun categoryItem() {
//        val numItemToShow = 10
//        val subsetCategoryItems = foodCategories.take(numItemToShow)
//
//        setFoodCategoryAdapter(subsetCategoryItems)
//    }
//    subsetFoodCategories: List<FoodCategory>
//    **********************        Adapter to show food category       *********************
private fun setFoodCategoryAdapter(){
    val adapter = FoodCategoryAdapter(foodCategories)

    homeFragmentBiding.recyclerViewCategory.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    homeFragmentBiding.recyclerViewCategory.adapter = adapter
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

    override fun onStart() {
        super.onStart()

    }
}