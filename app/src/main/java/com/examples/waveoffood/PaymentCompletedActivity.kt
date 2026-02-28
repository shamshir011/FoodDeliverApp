package com.examples.waveoffood

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.waveoffood.R
import com.example.waveoffood.databinding.ActivityPaymentCompletedBinding
import com.example.waveoffood.databinding.ActivityPaymentMethodBinding

class PaymentCompletedActivity : AppCompatActivity() {
    private val binding: ActivityPaymentCompletedBinding by lazy {
        ActivityPaymentCompletedBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.continueToHome.setOnClickListener {
            // Go to MainActivity or Home
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}