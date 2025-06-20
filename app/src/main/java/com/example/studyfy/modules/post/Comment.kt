package com.example.studyfy.modules.post

import com.google.firebase.Timestamp

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
