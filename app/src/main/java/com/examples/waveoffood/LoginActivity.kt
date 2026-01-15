package com.examples.waveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.waveoffood.R
import com.example.waveoffood.databinding.ActivityLoginBinding
import com.examples.waveoffood.Model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private var userName: String? = null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        //Initialize of firebase authentication
        auth = Firebase.auth
        //Initialize of firebase database
        database = Firebase.database.reference
        //Initialize of google
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.loginButton.setOnClickListener{
            // Get data from edit-text
            email = binding.editTextEmail.text.toString().trim()
            password = binding.editTextPassword.text.toString().trim()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please enter the all details", Toast.LENGTH_SHORT).show()
            }
            else{
                createUserAccount()
                Toast.makeText(this,"Login Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        binding.dontHaveButton.setOnClickListener{
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
        binding.googleButton.setOnClickListener{
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
    }

    private fun createUserAccount(){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful){
                val user = auth.currentUser
                updateUi(user)
            }
            else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        saveUserData()
                        val user = auth.currentUser
                        updateUi(user)
                    }
                    else{
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveUserData(){
        // Get data from text field
        email = binding.editTextEmail.text.toString().trim()
        password = binding.editTextPassword.text.toString().trim()
        val user = UserModel(userName,email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Save data in to database
        database.child("user").child(userId).setValue(user)
    }

//    Launcher for google sing-in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Login Successfully",Toast.LENGTH_SHORT).show()
                        updateUi(null)
                    }
                    else{
                        Toast.makeText(this,"Google sign-in failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Google sign-in failed",Toast.LENGTH_SHORT).show()
            }
        }
}

    private fun updateUi(user: FirebaseUser?){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart(){
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}