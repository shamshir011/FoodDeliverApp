package com.examples.waveoffood.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.waveoffood.databinding.FragmentCusineDishesBottomSheetBinding
import com.examples.waveoffood.Adapter.CuisinesDishesAdapter
import com.examples.waveoffood.Model.FoodCategory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CuisineDishesBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCusineDishesBottomSheetBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var cuisineDishes: MutableList<FoodCategory>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCusineDishesBottomSheetBinding.inflate(layoutInflater, container, false)

//        binding.buttonBack.setOnClickListener{
//            dismiss()
//        }



        retrieveCuisineDishes()

        return binding.root

    }

    private fun retrieveCuisineDishes(){
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("FoodCategory")
        cuisineDishes = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                for(foodSnapshot in snapshot.children){
                    val cuisineDish = foodSnapshot.getValue(FoodCategory::class.java)
                    cuisineDish?.let {
                        cuisineDishes.add(it)

//                        binding.cuisineDishProgressBar.visibility = View.INVISIBLE
//                        binding.cuisineDishProgressBar.visibility = View.VISIBLE
                    }
                }
                binding.cuisineDishProgressBar.visibility = View.GONE

                Log.d("ITEMS", "onDataChange: Data Received")
                //Once data receive set to adapter
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError){
                binding.cuisineDishProgressBar.visibility = View.GONE
            }
        })
    }


    private fun setAdapter() {
        if(cuisineDishes.isNotEmpty()){
            val adapter = CuisinesDishesAdapter(cuisineDishes, requireContext())
            binding.cuisineDishesRecyclerView.layoutManager = GridLayoutManager(requireContext(),4)
            binding.cuisineDishesRecyclerView.adapter = adapter
            Log.d("ITEMS", "setAdapter: data set")
        }else{
            Log.d("ITEMS", "setAdapter: data not set")
        }
    }
}