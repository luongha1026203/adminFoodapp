package com.example.foodappmanager.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodappmanager.R
import com.example.foodappmanager.databinding.ActivitySeclectAddBinding

class SeclectAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeclectAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeclectAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnQR.setOnClickListener {
            val intent = Intent(this, QrActivity::class.java)
            startActivity(intent)
        }
        binding.btnadd.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
    }
}