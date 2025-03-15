package com.example.studyfy.modules.library.quiz.quiz_list

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R
import com.example.studyfy.modules.library.quiz.QuizActivity

class QuizListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz_list)
        // ListView'yi bağla
        val listView: ListView = findViewById(R.id.quiz_list11)

        // Örnek veri (gerçek veriye göre güncellenebilir)
        val lessons = arrayOf("test 1", "test 2", "test 3")

        // ListView için adapter ayarla
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lessons)
        listView.adapter = adapter

        // Tıklama olayını ekle
        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}