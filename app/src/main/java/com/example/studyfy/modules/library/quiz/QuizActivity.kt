package com.example.studyfy.modules.library.quiz

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.studyfy.databinding.ActivityQuizBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.db.Quiz
import com.example.studyfy.R

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private var currentQuestionIndex = 0
    private var selectedAnswer: String = ""
    private lateinit var quiz: Quiz
    private var postList: List<Post> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val quizId = intent.getStringExtra("quizId")
        if (quizId.isNullOrEmpty()) {
            finish()
            return
        }

        FirestoreManager.getQuizById(quizId, { loadedQuiz ->
            if (loadedQuiz == null) {
                finish()
                return@getQuizById
            }

            quiz = loadedQuiz
            binding.quizTitleTextview.text = "Quiz"
            binding.subject.text = quiz.subject

            FirestoreManager.getPostsByIds(quiz.questions, { posts ->
                postList = posts
                binding.totalQuestions.text = posts.size.toString()
                showQuestion(currentQuestionIndex)
            }, {
                finish()
            })

        }, {
            finish()
        })

        // Cevap butonları dinleyicisi
        val answerButtons = listOf(
            binding.optionA, binding.optionB, binding.optionC, binding.optionD, binding.optionE
        )

        answerButtons.forEach { button ->
            button.setOnClickListener {
                selectedAnswer = button.text.toString()
                highlightSelected(button)
            }
        }

        binding.confirmButton.setOnClickListener {
            if (selectedAnswer.isNotEmpty()) {
                val updatedAnswers = quiz.userAnswers.toMutableList()
                updatedAnswers[currentQuestionIndex] = selectedAnswer
                quiz = quiz.copy(userAnswers = updatedAnswers)
                goToNextQuestion()
            }
        }

        binding.skipButton.setOnClickListener {
            goToNextQuestion()
        }
    }

    private fun showQuestion(index: Int) {
        if (index >= postList.size) {
            Toast.makeText(this, "Quiz tamamlandı!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val post = postList[index]
        binding.questionNumber.text = (index + 1).toString()
        selectedAnswer = ""

        // Resmi yükle
        Glide.with(this).load(post.imageUrl).into(binding.imageView6)

        // Butonları sıfırla
        val buttons = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD, binding.optionE)
        buttons.forEach {
            it.setBackgroundResource(R.drawable.button_background_green)
        }
    }

    private fun highlightSelected(selected: android.widget.Button) {
        val buttons = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD, binding.optionE)
        buttons.forEach {
            it.setBackgroundResource(R.drawable.button_background_green)
        }
        selected.setBackgroundResource(R.drawable.button_background_selected)
    }

    private fun goToNextQuestion() {
        currentQuestionIndex++
        showQuestion(currentQuestionIndex)
    }
}
