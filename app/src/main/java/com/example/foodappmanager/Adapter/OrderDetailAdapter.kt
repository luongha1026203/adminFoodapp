package com.example.foodappmanager.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodappmanager.databinding.OrderDetailItemBinding

class OrderDetailAdapter(
    private val context: Context,
    private var foodName: MutableList<String> = mutableListOf(),
    private var foodImage: MutableList<String> = mutableListOf(),
    private var foodQuantity: MutableList<Int> = mutableListOf(),
    private var foodPrice: MutableList<String> = mutableListOf()

) : RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = OrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
       return foodName.size
    }

    inner class ViewHolder(private val binding: OrderDetailItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                itemName.text = foodName[position]
                quantityItem.text = foodQuantity[position].toString()
                Glide.with(context).load(foodImage[position]).into(itemImage)
                price.text = foodPrice[position]+"$"
            }
        }
    }

}