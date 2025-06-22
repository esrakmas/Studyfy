package com.example.studyfy.modules.db


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object PostRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    private val postsCollection = db.collection("posts") // Düzeltildi

    // YENİ EKLENECEK METOT:
    fun getPostById(postId: String, onResult: (Post?) -> Unit) {
        postsCollection.document(postId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val post = document.toObject(Post::class.java)
                    onResult(post)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                // Hata durumunu loglayabilir veya kullanıcıya bildirebilirsiniz
                onResult(null)
            }
    }

    // Like toggle işlemi
    fun toggleLike(post: Post, onComplete: (updatedPost: Post?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(null)
        val postRef = db.collection("posts").document(post.postId)

        val isLiked = post.likes.contains(userId)

        val updateTask = if (isLiked) {
            postRef.update("likes", FieldValue.arrayRemove(userId))
        } else {
            postRef.update("likes", FieldValue.arrayUnion(userId))
        }

        updateTask.addOnSuccessListener {
            // Yeni listeyi güncelle
            val updatedLikes = if (isLiked) post.likes - userId else post.likes + userId
            val updatedPost = post.copy(likes = updatedLikes)
            onComplete(updatedPost)
        }.addOnFailureListener {
            onComplete(null)
        }
    }

    // Save toggle işlemi
    fun toggleSave(post: Post, onComplete: (updatedPost: Post?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(null)
        val postRef = db.collection("posts").document(post.postId)

        val isSaved = post.savedBy.contains(userId)

        val updateTask = if (isSaved) {
            postRef.update("savedBy", FieldValue.arrayRemove(userId))
        } else {
            postRef.update("savedBy", FieldValue.arrayUnion(userId))
        }

        updateTask.addOnSuccessListener {
            val updatedSavedBy = if (isSaved) post.savedBy - userId else post.savedBy + userId
            val updatedPost = post.copy(savedBy = updatedSavedBy)
            onComplete(updatedPost)
        }.addOnFailureListener {
            onComplete(null)
        }
    }
}