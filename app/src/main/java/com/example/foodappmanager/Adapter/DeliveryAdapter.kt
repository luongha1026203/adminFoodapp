package com.example.foodappmanager.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodappmanager.R
import com.example.foodappmanager.databinding.DeliveryItemBinding

class DeliveryAdapter(
    private val customerName: MutableList<String>,
    private val moneyStatus: MutableList<Boolean>
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveryViewHolder {
        val binding = DeliveryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DeliveryViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return customerName.size
    }

    inner class DeliveryViewHolder(private val binding: DeliveryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(i: Int) {
            binding.apply {
                customerNameTextView.text = customerName[i]
                if (moneyStatus[i] == true) {
                    moneyStatusTextView.text = "Received"
                } else {
                    moneyStatusTextView.text = "NotReceived"
                }
                val colorMap = mapOf(
                    true to Color.GREEN,
                    false to Color.RED
                )
                moneyStatusTextView.setTextColor(
                    colorMap[moneyStatus[i]] ?: Color.BLACK
                )
                statusColor.backgroundTintList =
                    ColorStateList.valueOf(colorMap[moneyStatus[i]] ?: Color.BLACK)
            }
        }
    }
}