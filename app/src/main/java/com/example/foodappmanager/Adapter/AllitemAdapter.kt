package com.example.foodappmanager.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodappmanager.databinding.ItemItemBinding
import com.example.foodappmanager.model.AllMenu
import com.google.firebase.database.DatabaseReference

class AllitemAdapter(
    private val context: Context,
    private val Menulist: ArrayList<AllMenu>,
    dataRef: DatabaseReference,
    private val onDeleteClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<AllitemAdapter.AllitemViewHolder>() {

    private val itemQuality = MutableList(Menulist.size) { 1 }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllitemViewHolder {
        val binding = ItemItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AllitemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AllitemViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return Menulist.size
    }

    inner class AllitemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(i: Int) {
            // Bảo vệ: tránh crash nếu dữ liệu không đồng bộ
            if (i >= Menulist.size || i >= itemQuality.size) return

            binding.apply {
                val quantity = itemQuality[i]
                itemName.text = Menulist[i].foodName
                itemPrice.text = Menulist[i].foodPrice
                Glide.with(context).load(Menulist[i].foodImage).into(itemImage)
                itemQuantity.text = quantity.toString()

                btnminus.setOnClickListener {
                    decreaseItemQuantity(i)
                }
                btnplus.setOnClickListener {
                    increaseItemQuantity(i)
                }
                itemDelete.setOnClickListener {
                    onDeleteClickListener(i)
                }
            }
        }

        private fun increaseItemQuantity(position: Int) {
            if (position < itemQuality.size && itemQuality[position] < 10) {
                itemQuality[position]++
                binding.itemQuantity.text = itemQuality[position].toString()
            }
        }

        private fun decreaseItemQuantity(position: Int) {
            if (position < itemQuality.size && itemQuality[position] > 1) {
                itemQuality[position]--
                binding.itemQuantity.text = itemQuality[position].toString()
            }
        }
    }
}
