package com.example.foodappmanager.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodappmanager.R
import com.example.foodappmanager.databinding.ActivityMainBinding
import com.example.foodappmanager.model.OrderDetails
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth                      // Firebase Authentication
    private lateinit var signClientGoogle: GoogleSignInClient   // Client đăng nhập Google
    private lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Khởi tạo Firebase Auth và Database
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference
        pendingOrder()
        competedOrder()
        WholeTimeEarning()
        // Cấu hình đăng nhập Google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Token để xác thực Firebase
            .requestEmail() // Yêu cầu lấy email người dùng
            .build()

        // Tạo client đăng nhập Google
        signClientGoogle = GoogleSignIn.getClient(this, googleSignInOptions)
        // Thiết lập các sự kiện nút
        controls()
    }

    private fun WholeTimeEarning() {
        val listPaymentTotal = mutableListOf<Int>()

        db.child("CompletedOrder").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               for (snap in snapshot.children){
                   var complete = snap.getValue(OrderDetails::class.java)
                   complete?.totalPrice?.replace("$","")?.toIntOrNull()
                       ?.let {
                           i-> listPaymentTotal.add(i)
                       }
               }
                binding.pay.text = listPaymentTotal.sum().toString() +"$"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun competedOrder() {
        var pendingRef = db.child("CompletedOrder")
        var pendingItemCount = 0
        pendingRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingItemCount = snapshot.childrenCount.toInt()
                binding.Completed.text = pendingItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun pendingOrder() {
        var pendingRef = db.child("OrderDetails")
        var pendingItemCount = 0
        pendingRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingItemCount = snapshot.childrenCount.toInt()
                binding.pendingOrderCount.text = pendingItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun controls() {
        binding.addItem.setOnClickListener {
            val intent = Intent(this, SeclectAddActivity::class.java)
            startActivity(intent)
        }
        binding.AllItem.setOnClickListener {
            val intent = Intent(this, AllitemActivity::class.java)
            startActivity(intent)
        }
        binding.order.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }
        binding.profile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }
        binding.user.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
        binding.pendingText.setOnClickListener {
            val intent = Intent(this, PendingActivity::class.java)
            startActivity(intent)
        }
        binding.logout.setOnClickListener {
            logout()
        }
    }

    fun logout() {
        // Đăng xuất Firebase
        auth.signOut()

        // Đăng xuất tài khoản Google
        signClientGoogle.signOut().addOnCompleteListener {
            // Sau khi sign out xong
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}