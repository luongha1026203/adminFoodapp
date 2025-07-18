package com.example.foodappmanager.Activities

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.foodappmanager.databinding.ActivityAddItemBinding
import com.example.foodappmanager.model.AllMenu
import com.example.foodappmanager.model.FileUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File

class AddItemActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddItemBinding.inflate(layoutInflater) }

    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredient: String
    private var foodImageUri: Uri? = null

    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uploadViewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        controls()
        observeUploadResult()
    }

    private fun controls() {
        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnback.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            foodName = binding.foodname.text.toString().trim()
            foodPrice = binding.foodprice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.Ingredients.text.toString().trim()

            if (foodName.isNotEmpty() && foodPrice.isNotEmpty()
                && foodDescription.isNotEmpty() && foodIngredient.isNotEmpty()
                && foodImageUri != null
            ) {
                val imagePath = FileUtil.getPath(this, foodImageUri!!)
                if (imagePath != null) {

                    val imageFile = File(imagePath)
                    uploadViewModel.uploadImage(imageFile)
                } else {
                    Toast.makeText(this, "Không thể truy cập file ảnh đã chọn", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeUploadResult() {
        uploadViewModel.uploadResult.observe(this) { result ->
            if (result.success && result.image_url != null) {
                saveToFirebase(result.image_url)
            } else {
                Toast.makeText(this, "Lỗi upload ảnh: ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToFirebase(imageUrl: String) {
        val menuItem = database.getReference("menu")
        val newItemKey = menuItem.push().key ?: return

        val newItem = AllMenu(
            newItemKey,
            foodName = foodName,
            foodPrice = foodPrice,
            foodDescription = foodDescription,
            foodIngredient = foodIngredient,
            foodImage = imageUrl
        )

        menuItem.child(newItemKey).setValue(newItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Thêm món thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
            }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            foodImageUri = it
            Glide.with(this)
                .load(foodImageUri)
                .override(800, 800)
                .centerCrop()
                .into(binding.selectdImage)

        }
    }
}
