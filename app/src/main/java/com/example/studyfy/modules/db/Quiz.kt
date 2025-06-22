package com.example.studyfy.modules.db

import com.google.firebase.Timestamp


data class Quiz(
    val quizId: String = "",
    val userId: String = "",
    val subject: String = "",
    val questions: List<String> = listOf(),       // postId listesi
    var userAnswers: List<String> = listOf(),     // Kullanıcının seçtiği şıklar (örneğin "A", "B", ...)
    val correctAnswers: List<String?> = listOf(),  // Doğru cevaplar
    val createdAt: Timestamp = Timestamp.now()
)