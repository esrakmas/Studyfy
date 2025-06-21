package com.example.studyfy.modules.library.questions

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.databinding.ActivityQuestionsLessonsBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.google.firebase.auth.FirebaseAuth

class QuestionsLessonsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionsLessonsBinding
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsLessonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSavedQuestionSubjects()
    }

    private fun loadSavedQuestionSubjects() {
        currentUserId?.let { userId ->
            FirestoreManager.getSavedPostsByType(userId, "question") { questions ->
                val distinctSubjects = questions.map { it.subject }.distinct()

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, distinctSubjects)
                binding.listQuestionsLessons.adapter = adapter

                binding.listQuestionsLessons.setOnItemClickListener { _, _, position, _ ->
                    val selectedSubject = distinctSubjects[position]
                    val intent = Intent(this, QuestionsActivity::class.java).apply {
                        putExtra("subject", selectedSubject)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}
