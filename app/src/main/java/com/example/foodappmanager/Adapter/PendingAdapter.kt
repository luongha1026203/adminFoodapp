package com.example.foodappmanager.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodappmanager.databinding.PendingItemBinding

class PendingAdapter(
    private val context: Context,
    private val customerName: MutableList<String>,
    private val quantity: MutableList<String>,
    private val imageFood: MutableList<String>,
    private val itemClick: OnItemClicked
) : RecyclerView.Adapter<PendingAdapter.PendingViewHolder>() {

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val binding = PendingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return customerName.size
    }

    inner class PendingViewHolder(private val binding: PendingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isAccepted = false

        fun bind(i: Int) {
            binding.apply {
                itemName.text = customerName[i]
                quantityItem.text = quantity[i]
                Glide.with(context).load(imageFood[i]).into(itemImage)
                btnAccept.apply {
                    // Cập nhật trạng thái nút
                    text = if (!isAccepted) "Accept" else "Dispatch"

                    setOnClickListener {
                        isAccepted = !isAccepted

                        if (isAccepted) {
                            // Khi bấm Accept, chỉ đổi text và hiển thị thông báo
                            text = "Dispatch"
                            showToast("Order Accepted: ${customerName.getOrNull(adapterPosition) ?: "Unknown"}")
                            itemClick.onItemAcceptClickListener(i)
                        } else {
                            // Lưu tên trước khi xoá
                            val name = customerName[adapterPosition]

                            // Xoá cả 3 danh sách để tránh lệch dữ liệu
                            customerName.removeAt(i)
                            quantity.removeAt(i)
                            imageFood.removeAt(i)

                            // Cập nhật giao diện RecyclerView
                            notifyItemRemoved(i)

                            // Thông báo
                            showToast("Order Dispatched: $name")
                            itemClick.onItemDispatchClickListener(i)
                        }
                    }
                }

                // Xử lý khi click vào toàn bộ item
                itemView.setOnClickListener {
                    itemClick.onItemClickListener(adapterPosition)
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
