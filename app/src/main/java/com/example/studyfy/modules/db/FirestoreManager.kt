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

    fun addComment(
        postId: String,
        content: String,
        username: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        val commentId = db.collection("posts").document(postId).collection("comments").document().id

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

    fun getComments(
        postId: String,
        onResult: (List<Comment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                val comments = snapshot.toObjects(Comment::class.java)
                onResult(comments)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getSavedPostsByType(
        userId: String,
        type: String,
        onResult: (List<Post>) -> Unit
    ) {
        db.collection("posts")
            .whereEqualTo("type", type)
            .whereArrayContains("savedBy", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.toObjects(Post::class.java)
                onResult(posts)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getUserPosts(
        userId: String,
        type: String,
        onResult: (List<Post>) -> Unit
    ) {
        db.collection("posts")
            .whereEqualTo("type", type)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.toObjects(Post::class.java)
                onResult(posts)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getAllSubjectsForUserQuestions(
        userId: String,
        onComplete: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userQuestions = db.collection("posts")
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", "question")

        val savedQuestions = db.collection("posts")
            .whereArrayContains("savedBy", userId)
            .whereEqualTo("type", "question")

        userQuestions.get().addOnSuccessListener { userSnap ->
            savedQuestions.get().addOnSuccessListener { savedSnap ->
                val allSubjects = mutableSetOf<String>()
                userSnap.documents.mapNotNullTo(allSubjects) { it.getString("subject") }
                savedSnap.documents.mapNotNullTo(allSubjects) { it.getString("subject") }
                onComplete(allSubjects.toList())
            }.addOnFailureListener(onFailure)
        }.addOnFailureListener(onFailure)
    }

    // Daha önce doğru cevaplanan postId’leri getir
    fun getCorrectlyAnsweredPostIds(
        userId: String,
        subject: String,
        onResult: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("quizzes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("subject", subject)
            .get()
            .addOnSuccessListener { snapshot ->
                val correctIds = mutableListOf<String>()
                for (doc in snapshot.documents) {
                    val quiz = doc.toObject(Quiz::class.java)
                    quiz?.questions?.forEachIndexed { index, postId ->
                        if (quiz.userAnswers.getOrNull(index) == quiz.correctAnswers.getOrNull(index)) {
                            correctIds.add(postId)
                        }
                    }
                }
                onResult(correctIds)
            }
            .addOnFailureListener(onFailure)
    }

    fun getUserAndSavedQuestions(
        userId: String,
        subject: String,
        onResult: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val allPosts = mutableListOf<Post>()

        getSavedPostsByType(userId, "question") { savedPosts ->
            allPosts.addAll(savedPosts.filter { it.subject == subject })

            getUserPosts(userId, "question") { userPosts ->
                allPosts.addAll(userPosts.filter { it.subject == subject })
                val distinctPosts = allPosts.distinctBy { it.postId }
                onResult(distinctPosts)
            }
        }
    }



    // Quiz'i Firestore'a kaydet
    fun saveQuizResult(
        quiz: Quiz,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = db.collection("quizzes").document()
        val quizToSave = quiz.copy(
            quizId = docRef.id,
            createdAt = Timestamp.now() // createdAt ekle
        )

        docRef.set(quizToSave)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

// FirestoreManager.kt içerisine

    fun getQuizzesBySubjectAndUser(
        userId: String,
        subject: String,
        onResult: (List<Quiz>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("quizzes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("subject", subject)

            .get()
            .addOnSuccessListener { snapshot ->
                val quizzes = snapshot.toObjects(Quiz::class.java)
                onResult(quizzes)
            }
            .addOnFailureListener { onFailure(it) }
    }
    fun getQuizById(
        quizId: String,
        onResult: (Quiz?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("quizzes")
            .document(quizId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val quiz = doc.toObject(Quiz::class.java)
                    onResult(quiz)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getPostIdsInUnansweredQuizzes(
        userId: String,
        subject: String,
        onResult: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirebaseFirestore.getInstance().collection("quizzes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("subject", subject)
            .get()
            .addOnSuccessListener { snapshot ->
                val usedPostIds = mutableListOf<String>()
                for (doc in snapshot.documents) {
                    val quiz = doc.toObject(Quiz::class.java)
                    if (quiz != null) {
                        quiz.userAnswers.forEachIndexed { index, answer ->
                            // Eğer kullanıcı henüz cevaplamamışsa
                            if (answer.isNullOrEmpty()) {
                                usedPostIds.add(quiz.questions.getOrNull(index) ?: "")
                            }
                        }
                    }
                }
                onResult(usedPostIds.filter { it.isNotEmpty() })
            }
            .addOnFailureListener { onFailure(it) }
    }


}
