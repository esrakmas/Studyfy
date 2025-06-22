package com.example.studyfy.repository

import User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Şu an giriş yapmış olan kullanıcıyı al
    fun getCurrentUser(onResult: (User?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // Belirli bir userId ile kullanıcıyı al (başka birinin profili için)
    fun getUserById(userId: String, onResult: (User?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
    fun getCurrentUsername(onResult: (String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(null)
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val username = document.getString("username")
                onResult(username)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    fun updateProfilePhotoUrl(photoUrl: String, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        db.collection("users").document(userId)
            .update("profileImageUrl", photoUrl)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // 🔥🔥 YENİ EKLEYECEĞİMİZ METOT: Tüm profil alanlarını güncelleyecek
    fun updateUserProfile(
        userId: String,
        username: String,
        biography: String,
        gradeLevel: String,
        profileImageUrl: String,
        onResult: (Boolean) -> Unit
    ) {
        val updates = hashMapOf<String, Any>(
            "username" to username,
            "biography" to biography,
            "gradeLevel" to gradeLevel,
            "profileImageUrl" to profileImageUrl
        )

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
