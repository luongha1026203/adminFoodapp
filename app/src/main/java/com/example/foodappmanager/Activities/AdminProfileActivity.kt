package com.example.foodappmanager.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodappmanager.R
import com.example.foodappmanager.databinding.ActivityAdminProfileBinding
import com.example.foodappmanager.model.UsersModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminRef = database.reference.child("Users")
        setfalseEdit()
        Controls()



    }

    private fun retrieveUserData() {
        val currentUser = auth.currentUser?.uid
        if (currentUser != null){
            val adminReff = adminRef.child(currentUser)
            adminReff.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var name = snapshot.child("username").value
                        var email = snapshot.child("email").value
                        var pass = snapshot.child("password").value
                        var address = snapshot.child("address").value
                        var phone = snapshot.child("phone").value
                        setdatatoView(name, email, pass, address, phone)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    private fun setdatatoView(name: Any?, email: Any?, pass: Any?, address: Any?, phone: Any?) {
            binding.nameInput.setText(name.toString())
            binding.emailInput.setText(email.toString())
        binding.passwordInput.setText(pass.toString())
        binding.addressInput.setText(address.toString())
        binding.phoneInput.setText(phone.toString())
    }

    private fun Controls() {
        binding.btnback.setOnClickListener {
            finish()
        }
        binding.saveButton.setOnClickListener {
            updateUserData()
        }
    }

    private fun updateUserData() {
        val currentUser = auth.currentUser?.uid
        var name = binding.nameInput.text.toString().trim()
        var email=binding.emailInput.text.toString().trim()
        var pass = binding.passwordInput.text.toString().trim()
        var address=binding.addressInput.text.toString().trim()
        var phone = binding.phoneInput.text.toString().trim()
        if (currentUser != null){
            val adminReff = adminRef.child(currentUser)

            adminReff.child("username").setValue(name)
            adminReff.child("email").setValue(email)
            adminReff.child("password").setValue(pass)
            adminReff.child("address").setValue(address)
            adminReff.child("phone").setValue(phone)
            Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
                auth.currentUser?.updateEmail(email)
                auth.currentUser?.updatePassword(pass)

        }
    }

    private fun setfalseEdit() {
        binding.apply {
            nameInput.isEnabled = false
            emailInput.isEnabled = false
            phoneInput.isEnabled = false
            addressInput.isEnabled = false
            passwordInput.isEnabled = false
            var isEditable = false
            editRight.setOnClickListener {
                isEditable = !isEditable
                nameInput.isEnabled = isEditable
                emailInput.isEnabled = isEditable
                phoneInput.isEnabled = isEditable
                addressInput.isEnabled = isEditable
                passwordInput.isEnabled = isEditable
                if (isEditable) {
                    nameInput.requestFocus()
                }
                saveButton.isEnabled = isEditable
            }
            retrieveUserData()
        }
    }
}