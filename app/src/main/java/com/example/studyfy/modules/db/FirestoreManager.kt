package com.example.studyfy.modules.db


import User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object FirestoreManager {
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun uploadPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("posts")
            .add(post.copy(createdAt = Timestamp(Date())))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getAllPosts(onSuccess: (List<Post>) -> Unit) {
        db.collection("posts")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { result ->
                val postList = result.documents.mapNotNull { it.toObject(Post::class.java) }
                onSuccess(postList)
            }
    }

    fun getAllUsers(onSuccess: (List<User>) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                onSuccess(users)
            }
    }
}