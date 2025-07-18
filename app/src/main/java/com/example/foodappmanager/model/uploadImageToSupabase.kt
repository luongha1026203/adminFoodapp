package com.example.foodappmanager.Activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

data class UploadResult(val success: Boolean, val image_url: String?, val message: String?)

class UploadViewModel : ViewModel() {

    private val _uploadResult = MutableLiveData<UploadResult>()
    val uploadResult: LiveData<UploadResult> = _uploadResult

    fun uploadImage(imageFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val supabaseUrl = "https://vphteueqgdbxamncyiwe.supabase.co"
            val supabaseApiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZwaHRldWVxZ2RieGFtbmN5aXdlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI1NzM5MjgsImV4cCI6MjA2ODE0OTkyOH0.HzjRGCdwo1dCppSoESc8G6WyDnWSfzgcT54-nexQLkI"
            val bucketName = "anhfood"
            val fileName = "image_${System.currentTimeMillis()}.${imageFile.extension.lowercase()}"

            val mimeType = "image/${imageFile.extension.lowercase()}"
            Log.d("UploadDebug", "Uploading file: ${imageFile.absolutePath}")
            Log.d("UploadDebug", "File size: ${imageFile.length()} bytes")
            Log.d("UploadDebug", "File type: $mimeType")

            if (!imageFile.exists() || imageFile.length() == 0L) {
                _uploadResult.postValue(
                    UploadResult(false, null, "File không tồn tại hoặc rỗng")
                )
                return@launch
            }

            val client = OkHttpClient()
            val requestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())

            val request = Request.Builder()
                .url("$supabaseUrl/storage/v1/object/$bucketName/$fileName")
                .addHeader("apikey", supabaseApiKey)
                .addHeader("Authorization", "Bearer $supabaseApiKey")
                .addHeader("Content-Type", mimeType)
                .put(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("UploadDebug", "Upload failed: ${e.message}")
                    _uploadResult.postValue(UploadResult(false, null, e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body?.string()
                    if (response.isSuccessful) {
                        val imageUrl =
                            "$supabaseUrl/storage/v1/object/public/$bucketName/$fileName"
                        Log.d("UploadDebug", "Upload success: $imageUrl")
                        _uploadResult.postValue(UploadResult(true, imageUrl, "Success"))
                    } else {
                        Log.e("UploadDebug", "Upload failed: ${response.code} - $bodyString")
                        _uploadResult.postValue(
                            UploadResult(false, null, "HTTP error ${response.code}: $bodyString")
                        )
                    }
                }
            })
        }
    }
}
