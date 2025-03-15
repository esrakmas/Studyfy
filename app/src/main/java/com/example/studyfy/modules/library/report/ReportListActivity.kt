package com.example.studyfy.modules.library.report

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R

class ReportListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_list)
        val listView: ListView = findViewById(R.id.report_list)

        // Örnek veri (gerçek veriye göre güncellenebilir)
        val lessons = arrayOf("02.09.2025", "list 2 ", "list3")

        // ListView için adapter ayarla
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lessons)
        listView.adapter = adapter

        // Tıklama olayını ekle
        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
    }
}