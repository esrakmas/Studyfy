package com.example.studyfy.modules.post

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object CommentRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun addComment(postId: String, content: String, username: String, onResult: (Boolean) -> Unit) {
        val commentId = UUID.randomUUID().toString()
        val comment = Comment(
            commentId = commentId,
            postId = postId,
            userId = currentUser?.uid ?: "",
            username = username,
            content = content
        )

        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .document(commentId)
            .set(comment)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }

        // Yorum sayısını artır (isteğe bağlı)
        firestore.collection("posts").document(postId)
            .update("commentsCount", com.google.firebase.firestore.FieldValue.increment(1))
    }

    fun getComments(postId: String, onResult: (List<Comment>) -> Unit) {
        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { result ->
                val comments = result.documents.mapNotNull { it.toObject(Comment::class.java) }
                onResult(comments)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
