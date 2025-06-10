package com.example.studyfy.modules.db

data class Post(
    val postId: String = "",
    val userId: String = "",
    val type: String = "", // "note" or "question"
    val subject: String = "",
    val topic: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val likes: List<String> = emptyList(),
    val savedBy: List<String> = emptyList(),
    val createdAt: com.google.firebase.Timestamp? = null
)