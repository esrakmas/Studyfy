package com.example.studyfy.modules.library.quiz.quiz_lessons

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.library.quiz.quiz_list.QuizListActivity
import com.google.firebase.auth.FirebaseAuth

class QuizLessonsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val currentUserId: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_lessons)

        listView = findViewById(R.id.list_quiz_lessons2)

        currentUserId?.let { userId ->
            FirestoreManager.getAllSubjectsForUserQuestions(
                userId,
                onComplete = { subjectList ->
                    if (subjectList.isEmpty()) {
                        Toast.makeText(this, "Soru bulunamadı.", Toast.LENGTH_SHORT).show()
                    } else {
                        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, subjectList)
                        listView.adapter = adapter

                        listView.setOnItemClickListener { _, _, position, _ ->
                            val selectedSubject = subjectList[position]
                            val intent = Intent(this, QuizListActivity::class.java)
                            intent.putExtra("subject", selectedSubject)
                            startActivity(intent)
                        }
                    }
                },
                onFailure = {
                    Toast.makeText(this, "Veriler alınamadı.", Toast.LENGTH_SHORT).show()
                }
            )
        } ?: Toast.makeText(this, "Kullanıcı oturumu yok.", Toast.LENGTH_SHORT).show()
    }
}
