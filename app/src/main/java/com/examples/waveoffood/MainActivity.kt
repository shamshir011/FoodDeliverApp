package com.examples.waveoffood

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.waveoffood.R
import com.example.waveoffood.databinding.ActivityMainBinding
import com.examples.waveoffood.Fragment.Notification_Bottom_Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    private  val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        val navController: NavController = findNavController(R.id.fragmentContainerView)
        val bottomNav: BottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

//        binding.notificationButton.setOnClickListener {
//            val bottomSheetDialog = Notification_Bottom_Fragment()
//            bottomSheetDialog.show(supportFragmentManager,"Test")
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.food_according_to_sick, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.diabetes){
            Toast.makeText(this, "Diabetes Diet", Toast.LENGTH_SHORT).show()
        }
        else if (item.itemId == R.id.weightLose){
            Toast.makeText(this, "Weight Lose Diet", Toast.LENGTH_SHORT).show()
        }
        else if(item.itemId == R.id.DeliveryDiet){
            Toast.makeText(this, "After Delivery Diet", Toast.LENGTH_SHORT).show()
        }
        else if(item.itemId == R.id.postpartumNutrition){
            Toast.makeText(this, "Postpartum Nutrition", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}