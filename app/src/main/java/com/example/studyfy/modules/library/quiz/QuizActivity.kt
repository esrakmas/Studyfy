package com.example.studyfy.modules.library.quiz

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Quiz

class QuizActivity : AppCompatActivity() {

    private lateinit var quizTitleTextView: TextView
    // İstersen diğer view'ları da bağla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        quizTitleTextView = findViewById(R.id.quiz_title_textview) // activity_quiz.xml'de eklemelisin

        val quizId = intent.getStringExtra("quizId")
        if (quizId == null) {
            finish()
            return
        }

        loadQuizDetails(quizId)
    }

    private fun loadQuizDetails(quizId: String) {
        FirestoreManager.getQuizById(quizId, { quiz ->
            if (quiz != null) {
                quizTitleTextView.text = "Quiz: ${quiz.quizId}" // veya istediğin başka bir şey
                // Burada quiz sorularını ve diğer detayları gösterme işlemini yapabilirsin
            } else {
                finish()
            }
        }, {
            finish()
        })
    }
}
