package com.examples.waveoffood

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.waveoffood.R
import com.example.waveoffood.databinding.ActivityRestaurantItemBinding

class RestaurantItemActivity : AppCompatActivity() {

    private val binding: ActivityRestaurantItemBinding by lazy {
        ActivityRestaurantItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backNavigation.setOnClickListener {
            finish()
        }
    }
}