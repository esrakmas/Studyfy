package com.example.studyfy.modules.db

import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.auth.FirebaseAuth

object FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun uploadPost(
        post: Post,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onFailure(Exception("Kullanıcı giriş yapmamış!"))
            return
        }

        val postWithUser = post.copy(
            userId = userId,
            createdAt = com.google.firebase.Timestamp.now()
        )

        db.collection("posts")
            .add(postWithUser)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
