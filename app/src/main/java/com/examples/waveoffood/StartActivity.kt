package com.examples.waveoffood

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.waveoffood.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity(){
    private val binding: ActivityStartBinding by lazy{

        ActivityStartBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(binding.root)

        binding.textView5.visibility = View.INVISIBLE

        binding.nextButton.setOnClickListener{
            val intent = Intent(this@StartActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}