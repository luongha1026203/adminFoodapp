package com.example.foodappmanager.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodappmanager.Adapter.AllitemAdapter
import com.example.foodappmanager.R
import com.example.foodappmanager.databinding.ActivityAllitemBinding
import com.example.foodappmanager.model.AllMenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllitemActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAllitemBinding.inflate(layoutInflater)
    }
    private lateinit var dbRef: DatabaseReference
    private var menuList = ArrayList<AllMenu>()
    private lateinit var adapter: AllitemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dbRef = FirebaseDatabase.getInstance().reference
        getMenuItem()
        back()
    }

    private fun getMenuItem() {
       dbRef.child("menu").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuList.clear()
                if (snapshot.exists()) {
                    for (foodSnapshot in snapshot.children) {
                        val food = foodSnapshot.getValue(AllMenu::class.java)
                        if (food != null) {
                            menuList.add(food)
                        }
                    }
                    initView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }

        })
    }

    private fun initView() {
         adapter = AllitemAdapter(this@AllitemActivity, menuList, dbRef) { position ->
            deleteMenuItem(position)
        }
        binding.rcAllItem.adapter = adapter
        binding.rcAllItem.setHasFixedSize(true)
        binding.rcAllItem.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun deleteMenuItem(position: Int) {
        val deletedItem = menuList[position]
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận xoá")
        builder.setMessage("Bạn có chắc muốn xoá món '${deletedItem.foodName}' không?")
        builder.setPositiveButton("Xoá") { dialog, _ ->
            val menuItemKey = deletedItem.key
            val foodmenuRef = menuItemKey?.let {
                dbRef.child("menu").child(it)
            }

            foodmenuRef?.removeValue()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đã xoá món", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Không xoá được món", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Huỷ") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }



    private fun back() {
        binding.btnback.setOnClickListener {
            finish()
        }
    }
}