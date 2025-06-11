package com.example.studyfy.modules.db

import com.google.firebase.firestore.FirebaseFirestore

object PostRepository {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")

    fun getUserPosts(userId: String, callback: (List<Post>) -> Unit) {
        postsCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.documents.mapNotNull { it.toObject(Post::class.java) }
                callback(posts)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
