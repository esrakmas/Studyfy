package com.example.studyfy.modules.db

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

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

        val newDocRef = db.collection("posts").document()
        val postWithAllData = post.copy(
            postId = newDocRef.id,
            userId = userId,
            createdAt = Timestamp.now()
        )

        newDocRef.set(postWithAllData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
