package com.examples.waveoffood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.waveoffood.databinding.FragmentSearchBinding
import com.examples.waveoffood.Adapter.MenuAdapter
import com.example.waveoffood.R
import com.examples.waveoffood.Model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {
    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        searchBinding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        //Retrieve menu item from database
        retrieveMenuItem()

        //Setup for search view
        setupSearchView()



        return searchBinding.root
    }

    private fun retrieveMenuItem() {
//        Get database reference
        database = FirebaseDatabase.getInstance()
//        Reference to the menu node
        val foodReference: DatabaseReference = database.reference.child("menu")
        foodReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }
            override fun onCancelled(error: DatabaseError){

            }
        })
    }

    private fun showAllMenu(){
        val filteredMenuItem = ArrayList(originalMenuItems)
        setAdapter(filteredMenuItem)
    }

    private fun setAdapter(filteredMenuItem: ArrayList<MenuItem>) {
        adapter = MenuAdapter(filteredMenuItem, requireContext())
        searchBinding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchBinding.menuRecyclerView.adapter = adapter
    }


    private fun setupSearchView() {
        searchBinding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean{
                filterMenuItems(query?:"")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean{
                filterMenuItems(newText?: "")
                return true
            }
        })
    }

    private fun filterMenuItems(query: String){
        val filteredMenuItems = originalMenuItems.filter{
            it.foodName?.contains(query, ignoreCase = true) == true
        }
        setAdapter(filteredMenuItems as ArrayList<MenuItem>)
    }
}


