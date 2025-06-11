package com.example.studyfy.modules.db

import android.content.Context
import android.util.Log

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback


object CloudinaryService {

    fun init(context: Context) {
        val config: HashMap<String, String> = hashMapOf(
            "cloud_name" to "dopfef7xr",          // Senin Cloud Name
            "api_key" to "686466379946146",       // Senin API Key (bu genelde numerik oluyor, verdiğin değer bu mu emin misin?)
            "api_secret" to "RsbH32s1fYiuvJ961gP7xaYMuV8" // Senin API Secret
        )
        MediaManager.init(context, config)
    }

    fun uploadImage(imagePath: String, onResult: (String?) -> Unit) {
        MediaManager.get().upload(imagePath)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    Log.d("CloudinaryUpload", "Başarılı yükleme: $url")
                    onResult(url)
                }

                override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                    Log.e("CloudinaryUpload", "Yükleme hatası: ${error.description}")
                    onResult(null)
                }

                override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {}
            })
            .dispatch()
    }

}
