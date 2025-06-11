package com.example.studyfy.modules.explore

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.getSystemService
import com.example.studyfy.R

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search) // XML dosyanın adı bu olmalı

        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView2)

        // 🔍 Arama çubuğunu odakla ve klavyeyi aç
        searchView.isIconified = false
        searchView.requestFocus()

        val imm = getSystemService<InputMethodManager>()
        imm?.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)

        // 🔄 Dinleme örneği (veri filtreleme)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                // Burada filtreleme işlemini yapabilirsin
                return true
            }
        })
    }
}
