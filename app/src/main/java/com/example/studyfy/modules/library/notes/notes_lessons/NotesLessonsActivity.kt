package com.example.studyfy.modules.library.notes.notes_lessons

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R
import com.example.studyfy.modules.library.notes.NotesActivity

class NotesLessonsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notes_lessons)


        // ListView'yi bağla
        val listView: ListView = findViewById(R.id.list_notes)

        // Örnek veri (gerçek veriye göre güncellenebilir)
        val lessons = arrayOf("Lesson 1", "Lesson 2", "Lesson 3")

        // ListView için adapter ayarla
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lessons)
        listView.adapter = adapter

        // Tıklama olayını ekle
        listView.setOnItemClickListener { parent, view, position, id ->
            // Burada QuestionsActivity'yi başlatıyoruz
            val intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)
        }


    }
}