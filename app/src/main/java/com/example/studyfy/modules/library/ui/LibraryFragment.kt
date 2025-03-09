package com.example.studyfy.modules.library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.studyfy.databinding.FragmentLibraryBinding
import com.example.studyfy.modules.notes.ui.NotesActivity
import com.example.studyfy.modules.notes_lessons.NotesLessonsActivity
import com.example.studyfy.modules.questions.ui.QuestionsActivity
import com.example.studyfy.modules.questions_lessons.QuestionsLessonsActivity
import com.example.studyfy.modules.quiz.ui.QuizActivity
import com.example.studyfy.modules.quiz_lessons.QuizLessonsActivity
import com.example.studyfy.modules.report.ReportActivity
import com.example.studyfy.modules.report.ReportListActivity
import com.example.studyfy.modules.saved.ui.SavedActivity
import com.example.studyfy.modules.settings.ui.SettingsActivity

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewBinding kullanarak layout'u şişir
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Kaydedilenler tıklama olayı
        binding.savedPostsLayout.setOnClickListener {
            val intent = Intent(activity, SavedActivity::class.java)
            startActivity(intent)
        }

        // Notlar tıklama olayı
        binding.notesLayout.setOnClickListener {
            val intent = Intent(activity, NotesLessonsActivity::class.java)
            startActivity(intent)
        }

        // Sorular tıklama olayı
        binding.questionsLayout.setOnClickListener {
            val intent = Intent(activity, QuestionsLessonsActivity::class.java)
            startActivity(intent)
        }

        // Deneme tıklama olayı
        binding.testsLayout.setOnClickListener {
            val intent = Intent(activity, QuizLessonsActivity::class.java)
            startActivity(intent)
        }

        // Raporlar tıklama olayı
        binding.reportsLayout.setOnClickListener {
            val intent = Intent(activity, ReportListActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Bellek sızıntılarını önlemek için
    }
}
