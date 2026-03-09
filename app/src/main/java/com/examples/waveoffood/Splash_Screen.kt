package com.examples.waveoffood

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.waveoffood.R
import com.google.firebase.auth.FirebaseAuth

class Splash_Screen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser = auth.currentUser

            if (currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            finish()

        }, 3000)
    }
}