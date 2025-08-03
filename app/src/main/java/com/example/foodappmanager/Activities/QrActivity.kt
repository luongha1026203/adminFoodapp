package com.example.foodappmanager.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodappmanager.databinding.ActivityQrBinding
import com.example.foodappmanager.model.AllMenu
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import org.json.JSONObject

class QrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding
    private val database = FirebaseDatabase.getInstance()
    private val IMAGE_PICK_CODE = 1001

    // Quét bằng camera
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            handleQRResult(result.contents)
        } else {
            Toast.makeText(this, "Không đọc được mã QR", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Quét bằng camera ngay khi mở
        startQRScan()

        // Quét từ ảnh
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    private fun startQRScan() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Đưa mã QR vào khung hình")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        barcodeLauncher.launch(options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                decodeQRCodeFromBitmap(bitmap)
            }
        }
    }

    private fun decodeQRCodeFromBitmap(bitmap: Bitmap) {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = MultiFormatReader().decode(binaryBitmap)
            handleQRResult(result.text)
        } catch (e: NotFoundException) {
            Toast.makeText(this, "Không tìm thấy mã QR trong ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleQRResult(content: String) {
        try {
            val json = JSONObject(content)
            val id = database.getReference("menu").push().key ?: return
            val item = AllMenu(
                id,
                foodName = json.getString("foodName"),
                foodPrice = json.getString("foodPrice"),
                foodDescription = json.getString("foodDescription"),
                foodIngredient = json.getString("foodIngredient"),
                foodImage = json.getString("foodImage")
            )

            database.getReference("menu").child(id).setValue(item)
                .addOnSuccessListener {
                    Toast.makeText(this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Thêm món ăn thất bại", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "QR không đúng định dạng", Toast.LENGTH_SHORT).show()
        }
    }
}
