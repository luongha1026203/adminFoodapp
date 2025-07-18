package com.example.foodappmanager.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodappmanager.databinding.ActivitySignBinding
import com.example.foodappmanager.model.UsersModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignActivity : AppCompatActivity() {
    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }
    private lateinit var Auth : FirebaseAuth
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var username : String
    private lateinit var nameRestaurant : String
    private lateinit var Database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Auth = FirebaseAuth.getInstance()
        Database = FirebaseDatabase.getInstance().getReference("Users")
        val listLocation = arrayOf("Hanoi", "Ho Chi Minh", "Da Nang", "Hai Phong", "Can Tho")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, listLocation)
        val autoCompleteTextView = binding.listLocation
        autoCompleteTextView.setAdapter(adapter)
        binding.btnCreate.setOnClickListener {
            email = binding.edtemail.text.toString()
            password = binding.edtpassword.text.toString()
            username = binding.edtname.text.toString()
            nameRestaurant = binding.edthome.text.toString()
            if (email.isBlank() || password.isBlank() || username.isBlank() || nameRestaurant.isBlank()) {
                binding.edtemail.error = "Please fill in all fields"
            }else {
                createAccount(email, password)
            }

        }
        binding.txtsignin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun createAccount(email: String, password: String) {
        Auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            task->
            if (task.isSuccessful) {
                Toast.makeText(application, "Account created successfully", Toast.LENGTH_SHORT).show()
                savedata()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Account creation failed
                Toast.makeText(application, "Account created failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount:Failed",task.exception)
            }
        }
    }

    private fun savedata() {
        email = binding.edtemail.text.toString()
        password = binding.edtpassword.text.toString()
        username = binding.edtname.text.toString()
        nameRestaurant = binding.edthome.text.toString()
        val user = UsersModel(username,email,password,nameRestaurant)
        val userId = Auth.currentUser!!.uid
        //save user data to Firebase Realtime Database
        Database.child(userId).setValue(user)
    }
}