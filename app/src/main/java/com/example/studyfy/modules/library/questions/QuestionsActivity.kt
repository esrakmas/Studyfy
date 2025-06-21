package com.example.studyfy.modules.library.questions

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.databinding.ActivityQuestionsBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.post.PostGridActivityAdapter
import com.example.studyfy.modules.post.PostGridFragmentAdapter
import com.google.firebase.auth.FirebaseAuth

class QuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionsBinding
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val subject = intent.getStringExtra("subject") ?: return
        binding.titleText.text = "$subject Soruları"

        loadQuestionsBySubject(subject)
    }

    private fun loadQuestionsBySubject(subject: String) {
        currentUserId?.let { userId ->
            FirestoreManager.getSavedPostsByType(userId, "question") { allQuestions ->
                val filteredQuestions = allQuestions.filter { it.subject == subject }

                if (filteredQuestions.isEmpty()) {
                    Toast.makeText(this, "Bu derse ait soru bulunamadı.", Toast.LENGTH_SHORT).show()
                }

                val adapter = PostGridActivityAdapter(this, filteredQuestions)
                binding.gridSaved.adapter = adapter
            }
        }
    }
}
