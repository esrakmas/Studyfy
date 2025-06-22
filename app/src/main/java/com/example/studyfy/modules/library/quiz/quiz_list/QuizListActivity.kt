package com.example.studyfy.modules.library.quiz.quiz_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Quiz
import com.example.studyfy.modules.library.quiz.QuizActivity
import com.example.studyfy.modules.library.quiz.QuizCreator
import com.google.firebase.auth.FirebaseAuth

class QuizListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val currentUserId: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    private var quizList = listOf<Quiz>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        listView = findViewById(R.id.quiz_list11)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val selectedSubject = intent.getStringExtra("subject")

        if (userId == null || selectedSubject.isNullOrEmpty()) {
            Toast.makeText(this, "Kullanıcı ya da konu seçimi yok", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        QuizCreator.checkAndCreateQuizzes(userId, selectedSubject, { created ->
            if (created) {
                Toast.makeText(this, "Yeni quiz(ler) oluşturuldu.", Toast.LENGTH_SHORT).show()
            }
            loadQuizzes(userId, selectedSubject)
        }, { e ->
            Toast.makeText(this, "Quiz oluşturulurken hata: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }


    private fun loadQuizzes(userId: String, subject: String) {
        FirestoreManager.getQuizzesBySubjectAndUser(userId, subject, { quizzes ->
            quizList = quizzes
            if (quizzes.isEmpty()) {
                Toast.makeText(this, "Quiz bulunamadı.", Toast.LENGTH_SHORT).show()
                return@getQuizzesBySubjectAndUser
            }

            val quizNames = quizzes.mapIndexed { index, _ -> "Quiz ${index + 1}" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, quizNames)
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedQuiz = quizList[position]
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("quizId", selectedQuiz.quizId)
                startActivity(intent)
            }

        }, {
            Toast.makeText(this, "Quizler yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show()
        })
    }
}
