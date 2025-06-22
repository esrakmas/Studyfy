package com.example.studyfy.modules.library.quiz

import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Post

object QuizRepository {

    fun getAllQuizCandidateQuestions(
        userId: String,
        subject: String,
        onResult: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirestoreManager.getCorrectlyAnsweredPostIds(
            userId = userId,
            subject = subject,
            onResult = { correctIds ->
                // Kullanıcının hem kendi hem de kaydettiği tüm soruları al
                getUserAndSavedPostsByType(userId, "question") { allQuestions ->
                    // Daha önce doğru yapılmamış olanları filtrele
                    val eligibleQuestions = allQuestions.filter {
                        it.subject == subject && !correctIds.contains(it.postId)
                    }
                    onResult(eligibleQuestions)
                }
            },
            onFailure = onFailure
        )
    }

    private fun getUserAndSavedPostsByType(
        userId: String,
        type: String,
        onResult: (List<Post>) -> Unit
    ) {
        val allPosts = mutableListOf<Post>()

        FirestoreManager.getSavedPostsByType(userId, type) { savedPosts ->
            allPosts.addAll(savedPosts)

            FirestoreManager.getUserPosts(userId, type) { userPosts ->
                allPosts.addAll(userPosts)

                val distinctPosts = allPosts.distinctBy { it.postId }
                onResult(distinctPosts)
            }
        }
    }
}
