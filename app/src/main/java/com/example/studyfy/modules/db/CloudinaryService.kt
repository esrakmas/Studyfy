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

    fun uploadImage(imageUri: String, onResult: (String?) -> Unit) {
        MediaManager.get().upload(imageUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    Log.d("CLOUDINARY_UPLOAD", "Upload Success: $resultData")  // BU SATIRI EKLE
                    val url = resultData["secure_url"] as? String
                    onResult(url)
                }


                override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                    onResult(null)
                }

                override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {}
            })
            .dispatch()
    }

}
