package com.example.foodappmanager.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodappmanager.Adapter.DeliveryAdapter
import com.example.foodappmanager.R
import com.example.foodappmanager.databinding.ActivityOutForDeliveryBinding
import com.example.foodappmanager.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private var listofCompleteOrderList : ArrayList<OrderDetails> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        retrieveCompleteOrderDetails()

        back()
    }

    private fun retrieveCompleteOrderDetails() {
        database = FirebaseDatabase.getInstance()
        val completeOrderRef = database.getReference("CompletedOrder").orderByChild("currentTime")
        completeOrderRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listofCompleteOrderList.clear()
                for (snap in snapshot.children){
                    val completeOrder = snap.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listofCompleteOrderList.add(it)
                    }
                }
                listofCompleteOrderList.reversed()
                setAdapter()
            }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun setAdapter() {
        val customname = mutableListOf<String>()
        val Moneystatus = mutableListOf<Boolean>()
        for (order in listofCompleteOrderList){
            order.userName?.let {
                customname.add(it)
            }
            Moneystatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(customname,Moneystatus)
        binding.rcDelivery.adapter = adapter
        binding.rcDelivery.setHasFixedSize(true)
        binding.rcDelivery.layoutManager = LinearLayoutManager(this)
    }
    private fun back() {
        binding.btnback.setOnClickListener {
            finish()
        }
    }
}