package com.example.studyfy.modules.db

import com.example.studyfy.modules.post.Comment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

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
    fun getPostsByIds(
        postIds: List<String>,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (postIds.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        // Firestore whereIn sorgusunda max 10 id sorgulanabilir
        val chunks = postIds.chunked(10)
        val allPosts = mutableListOf<Post>()
        var completed = 0
        for (chunk in chunks) {
            db.collection("posts")
                .whereIn("postId", chunk)
                .get()
                .addOnSuccessListener { snapshot ->
                    val posts = snapshot.toObjects(Post::class.java)
                    allPosts.addAll(posts)
                    completed++
                    if (completed == chunks.size) {
                        onSuccess(allPosts)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }

    fun updateQuizUserAnswers(
        quizId: String,
        userAnswers: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("quizzes")
            .document(quizId)
            .update("userAnswers", userAnswers)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


    // YENİ EKLENECEK METOT: Kullanıcıya ait tüm verileri silme
    val postsCollection = db.collection("posts")
    fun deleteUserData(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // A list to hold all the individual deletion/update tasks
        val tasks = mutableListOf<Task<Void>>() // Explicitly type as Task<Void> for delete/update operations

        // 1. Delete the user's own document in the 'users' collection
        val userDocRef = db.collection("users").document(userId)
        tasks.add(userDocRef.delete())

        // 2. Delete all posts created by the user

        val getPostsTask: Task<Void> = postsCollection.whereEqualTo("userId", userId).get()
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    val postDeletionTasks = mutableListOf<Task<Void>>()
                    for (doc in task.result!!.documents) {
                        postDeletionTasks.add(doc.reference.delete()) // Add post deletion to list
                    }
                    Tasks.whenAll(postDeletionTasks) // Wait for all post deletions to complete
                } else {
                    Tasks.forException(task.exception ?: Exception("Failed to retrieve user posts"))
                }
            }
        tasks.add(getPostsTask)


        // 3. Delete comments made by the user across all posts (using collection group query)
        // This query can be costly for large datasets. Consider Cloud Functions for scale.
        val getCommentsTask: Task<Void> = db.collectionGroup("comments")
            .whereEqualTo("userId", userId)
            .get()
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    val commentDeletionTasks = mutableListOf<Task<Void>>()
                    for (doc in task.result!!.documents) {
                        commentDeletionTasks.add(doc.reference.delete()) // Add comment deletion to list
                    }
                    Tasks.whenAll(commentDeletionTasks) // Wait for all comment deletions to complete
                } else {
                    Tasks.forException(task.exception ?: Exception("Failed to retrieve user comments"))
                }
            }
        tasks.add(getCommentsTask)


        // 4. Delete all quiz results for the user
        val getQuizzesTask: Task<Void> = db.collection("quizzes").whereEqualTo("userId", userId).get()
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    val quizDeletionTasks = mutableListOf<Task<Void>>()
                    for (doc in task.result!!.documents) {
                        quizDeletionTasks.add(doc.reference.delete()) // Add quiz deletion to list
                    }
                    Tasks.whenAll(quizDeletionTasks) // Wait for all quiz deletions to complete
                } else {
                    Tasks.forException(task.exception ?: Exception("Failed to retrieve user quizzes"))
                }
            }
        tasks.add(getQuizzesTask)


        // 5. Remove user's ID from 'savedBy' arrays in other users' posts
        // This is also potentially slow and costly if many posts are saved by the user.
        val updateSavedByTask: Task<Void> = postsCollection.whereArrayContains("savedBy", userId).get()
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    val batch = db.batch() // Use a batch for multiple updates for efficiency
                    for (doc in task.result!!.documents) {
                        batch.update(doc.reference, "savedBy", FieldValue.arrayRemove(userId))
                    }
                    batch.commit() // Commit the batch as a single task
                } else {
                    Tasks.forException(task.exception ?: Exception("Failed to retrieve saved posts"))
                }
            }
        tasks.add(updateSavedByTask)


        // After all individual data retrieval and sub-deletion tasks are added,
        // wait for all of them to complete.
        Tasks.whenAll(tasks) // Use Tasks.whenAll for a list of Task<Void>
            .addOnSuccessListener {
                onSuccess() // All data deletion tasks completed successfully
            }
            .addOnFailureListener { e ->
                // If any of the deletion tasks failed, this will be called
                onFailure(e)
            }

}
}