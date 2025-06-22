package com.example.studyfy.modules.library.notes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.databinding.ActivityNotesBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.post.PostGridActivityAdapter
import com.google.firebase.auth.FirebaseAuth

class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesBinding
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val subject = intent.getStringExtra("subject") ?: return

        binding.titleText.text = "$subject Notları"

        loadNotesBySubject(subject)
    }

    private fun loadNotesBySubject(subject: String) {
        currentUserId?.let { userId ->
            FirestoreManager.getSavedPostsByType(userId, "note") { allNotes ->
                val filteredNotes = allNotes.filter { it.subject == subject }

                if (filteredNotes.isEmpty()) {
                    Toast.makeText(this, "Bu derse ait not bulunamadı.", Toast.LENGTH_SHORT).show()
                }

                val adapter = PostGridActivityAdapter(this, filteredNotes)
                binding.gridSaved.adapter = adapter
            }
        }
    }
}
