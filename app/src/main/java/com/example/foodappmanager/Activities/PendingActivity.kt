package com.example.foodappmanager.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodappmanager.Adapter.PendingAdapter
import com.example.foodappmanager.databinding.ActivityPendingBinding
import com.example.foodappmanager.model.OrderDetails
import com.google.firebase.database.*

class PendingActivity : AppCompatActivity(), PendingAdapter.OnItemClicked {

    // Sử dụng View Binding để ánh xạ layout
    private val binding by lazy {
        ActivityPendingBinding.inflate(layoutInflater)
    }

    // Danh sách dữ liệu phục vụ RecyclerView
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    // Firebase database reference
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.getReference("OrderDetails")

        // Gọi hàm lấy dữ liệu đơn hàng
        getOrderDetails()
        controls()
    }

    // Lấy danh sách đơn hàng từ Firebase
    private fun getOrderDetails() {
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemsnap in snapshot.children) {
                    val order = itemsnap.getValue(OrderDetails::class.java)
                    order?.let {
                        listOfOrderItem.add(it) // Thêm vào danh sách đơn hàng
                    }
                }
                addDataRyclerView() // Cập nhật RecyclerView sau khi load xong
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: Xử lý lỗi khi truy cập database
            }
        })
    }

    // Xử lý dữ liệu để chuẩn bị đưa vào Adapter
    private fun addDataRyclerView() {
        for (order in listOfOrderItem) {
            order.userName?.let { listOfName.add(it) }
            order.totalPrice?.let { listOfTotalPrice.add(it) }

            // Lấy ảnh đầu tiên trong danh sách ảnh món ăn (nếu có)
            order.foodImages?.filterNot {
                it.isEmpty()
            }?.forEach {
                listOfImageFirstFoodOrder.add(it)
            }
        }
        setAdapter() // Cài đặt Adapter cho RecyclerView
    }

    // Cài đặt Adapter và LayoutManager
    private fun setAdapter() {
        binding.rcallpending.layoutManager = LinearLayoutManager(this)
        val adapter = PendingAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, this)
        binding.rcallpending.adapter = adapter
    }

    // Xử lý sự kiện click nút back
    private fun controls() {
        binding.btnback.setOnClickListener {
            finish()
        }
    }

    // Khi người dùng nhấn vào item -> chuyển sang màn hình chi tiết đơn hàng
    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("userOrder", userOrderDetails)
        startActivity(intent)
    }

    // Khi admin nhấn "Chấp nhận" đơn hàng
    override fun onItemAcceptClickListener(position: Int) {
        val childItemPushKey: String? = listOfOrderItem[position].itemPushKey

        // Tham chiếu đến đơn hàng được chọn trong Firebase
        val clickItemOrderReference: DatabaseReference? = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }

        // Đánh dấu đơn hàng đã được chấp nhận
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)

        // Cập nhật trạng thái Accepted ở cả OrderDetails và lịch sử người dùng
        updateOrderAcceptStatus(position)
    }

    // Khi admin nhấn "Giao hàng"
    override fun onItemDispatchClickListener(position: Int) {
        val dispatchItemPushKey: String? = listOfOrderItem[position].itemPushKey

        // Tạo tham chiếu đến node CompletedOrder để lưu đơn hàng đã giao
        val dispatchItemOrderReference: DatabaseReference =
            database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)

        // Chuyển đơn hàng sang node CompletedOrder và xoá khỏi OrderDetails
        dispatchItemOrderReference
            .setValue(listOfOrderItem[position])
            .addOnSuccessListener {
                deleteThisItemFromOrderDetails(dispatchItemPushKey)
            }
    }

    // Xoá đơn hàng khỏi node OrderDetails sau khi đã chuyển sang CompletedOrder
    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderItemDetailsReference = database.getReference("OrderDetails").child(dispatchItemPushKey)
        orderItemDetailsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Đơn hàng đã được giao", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Giao hàng thất bại", Toast.LENGTH_LONG).show()
            }
    }

    // Cập nhật trạng thái Accepted cho cả OrderDetails và lịch sử mua hàng của người dùng
    private fun updateOrderAcceptStatus(position: Int) {
        val userIdOfClickedItem: String? = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem: String? = listOfOrderItem[position].itemPushKey

        val buyHistoryReference: DatabaseReference =
            database.reference
                .child("Users")
                .child(userIdOfClickedItem!!) // ID người dùng
                .child("orderHistory")
                .child(pushKeyOfClickedItem!!) // Mã đơn hàng

        // Cập nhật cờ "AcceptedOrder = true" ở cả hai nơi
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }
}
