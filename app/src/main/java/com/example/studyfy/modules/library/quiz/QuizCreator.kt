package com.example.studyfy.modules.library.quiz

import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Quiz
import com.google.firebase.Timestamp

object QuizCreator {
    fun checkAndCreateQuizzes(
        userId: String,
        subject: String,
        onComplete: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirestoreManager.getCorrectlyAnsweredPostIds(userId, subject, { correctIds ->

            FirestoreManager.getPostIdsInUnansweredQuizzes(userId, subject, { unansweredUsedIds ->

                FirestoreManager.getUserAndSavedQuestions(userId, subject, { allQuestions ->

                    val eligibleQuestions = allQuestions.filter {
                        it.postId !in correctIds && it.postId !in unansweredUsedIds
                    }

                    val chunks = eligibleQuestions.chunked(5)
                    if (chunks.isEmpty()) {
                        onComplete(false)
                        return@getUserAndSavedQuestions
                    }

                    val createdCount = intArrayOf(0)
                    chunks.forEach { questionGroup ->
                        val quiz = questionGroup.map { it.correctAnswer }?.let {
                            Quiz(
                                quizId = "",
                                userId = userId,
                                subject = subject,
                                questions = questionGroup.map { it.postId },
                                userAnswers = List(questionGroup.size) { "" },
                                correctAnswers = it,
                                createdAt = Timestamp.now()
                            )
                        }

                        if (quiz != null) {
                            FirestoreManager.saveQuizResult(quiz, {
                                createdCount[0]++
                                if (createdCount[0] == chunks.size) {
                                    onComplete(true)
                                }
                            }, onFailure)
                        }
                    }

                }, onFailure)

            }, onFailure)

        }, onFailure)
    }

}
