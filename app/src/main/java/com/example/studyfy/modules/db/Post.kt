package com.example.studyfy.modules.db

import com.google.firebase.Timestamp

data class Post(
    val postId: String = "",
    val userId: String = "",
    val type: String = "",       // "note" veya "question"
    val subject: String = "",
    val topic: String = "",      // selectedClass
    val description: String = "",
    val imageUrl: String = "",   // String olmalı (URL)
    val isPrivate: Boolean = false,
    val likes: List<String> = emptyList(),
    val savedBy: List<String> = emptyList(),
    val commentsCount: Int = 0,  // Eğer kullanıyorsan, yoksa kaldır
    val createdAt: Timestamp? = null
)
