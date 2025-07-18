package com.example.foodappmanager.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodappmanager.Adapter.OrderDetailAdapter
import com.example.foodappmanager.databinding.ActivityOrderDetailBinding
import com.example.foodappmanager.model.OrderDetails

class OrderDetailActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailBinding by lazy {
        ActivityOrderDetailBinding.inflate(layoutInflater)
    }
    private var userName: String? = null
    private var address: String? = null
    private var phoneNumber: String? = null
    private var totalPrice: String? = null

    private var foodNames: MutableList<String> = mutableListOf()
    private var foodImages: MutableList<String> = mutableListOf()
    private var foodQuantity: MutableList<Int> = mutableListOf()
    private var foodPrices: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.back.setOnClickListener {
            finish()
        }
        getDatafonIntent()
    }

    private fun getDatafonIntent() {
        val receivedIntent = intent.getParcelableExtra<OrderDetails>("userOrder")
        if (receivedIntent != null){
            userName = receivedIntent.userName
            address = receivedIntent.address
            phoneNumber =receivedIntent.phoneNumber
            foodPrices = receivedIntent.foodPrices!!
            foodNames =receivedIntent.foodNames!!
            foodImages =receivedIntent.foodImages!!
            foodQuantity =receivedIntent.foodQuantities!!
            totalPrice = receivedIntent.totalPrice
            setUserDetail()
            setAdapter()
        }
    }

    private fun setAdapter() {
        binding.rcOrderDetail.layoutManager = LinearLayoutManager(this)
        binding.rcOrderDetail.adapter = OrderDetailAdapter(this,foodNames,foodImages,foodQuantity,foodPrices)
    }

    private fun setUserDetail() {
        binding.name.text = userName
        binding.address.text = address
        binding.phone.text = phoneNumber
        binding.totalAmount.text = totalPrice
    }
}