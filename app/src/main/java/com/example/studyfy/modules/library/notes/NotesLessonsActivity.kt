package com.example.studyfy.modules.library.notes

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.databinding.ActivityNotesLessonsBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.google.firebase.auth.FirebaseAuth

class NotesLessonsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesLessonsBinding
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesLessonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSavedNoteSubjects()
    }

    private fun loadSavedNoteSubjects() {
        currentUserId?.let { userId ->
            FirestoreManager.getSavedPostsByType(userId, "note") { notes ->
                val distinctSubjects = notes.map { it.subject }.distinct()

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, distinctSubjects)
                binding.listNotes.adapter = adapter

                binding.listNotes.setOnItemClickListener { _, _, position, _ ->
                    val selectedSubject = distinctSubjects[position]
                    val intent = Intent(this, NotesActivity::class.java).apply {
                        putExtra("subject", selectedSubject)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}
