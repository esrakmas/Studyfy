package com.example.studyfy.firebase.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class StorageRepository {
    private val storage = FirebaseStorage.getInstance().reference

    fun uploadImage(fileUri: Uri, callback: (String?) -> Unit) {
        val fileRef = storage.child("images/${UUID.randomUUID()}.jpg")
        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
