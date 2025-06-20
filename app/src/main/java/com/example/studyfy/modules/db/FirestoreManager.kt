package com.example.studyfy.modules.db

import com.example.studyfy.modules.post.Comment
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

    fun addComment(postId: String, content: String, username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val commentId = FirebaseFirestore.getInstance().collection("posts")
            .document(postId).collection("comments").document().id

        val comment = Comment(
            commentId = commentId,
            postId = postId,
            userId = userId,
            username = username,
            content = content
        )

        db.collection("posts")
            .document(postId)
            .collection("comments")
            .document(commentId)
            .set(comment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getComments(postId: String, onResult: (List<Comment>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val comments = snapshot.toObjects(Comment::class.java)
                onResult(comments)
            }
            .addOnFailureListener { onFailure(it) }
    }

}
