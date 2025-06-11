package com.example.studyfy.modules.explore

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.studyfy.databinding.ActivitySearchBinding
import com.google.android.material.tabs.TabLayoutMediator

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val tabTitles = arrayOf("Notlar", "Sorular", "Hesaplar")

    // Fragment referanslarını saklayalım ki arama sorgusunu gönderebilelim
    private lateinit var searchNoteFragment: SearchNoteFragment
    private lateinit var searchQuestionsFragment: SearchQuestionsFragment
    private lateinit var searchUserFragment: SearchUserFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView2.isIconified = false
        binding.searchView2.requestFocus()

        val imm = getSystemService<InputMethodManager>()
        imm?.showSoftInput(binding.searchView2.findFocus(), InputMethodManager.SHOW_IMPLICIT)

        // Fragmentları oluşturuyoruz
        searchNoteFragment = SearchNoteFragment()
        searchQuestionsFragment = SearchQuestionsFragment()
        searchUserFragment = SearchUserFragment()

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> searchNoteFragment
                1 -> searchQuestionsFragment
                2 -> searchUserFragment
                else -> throw IllegalStateException("Pozisyon bulunamadı")
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // İstersen buraya da aynı arama gönderilebilir
                query?.let { searchInCurrentTab(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchInCurrentTab(it) }
                return true
            }
        })
    }

    private fun searchInCurrentTab(query: String) {
        when (binding.viewPager.currentItem) {
            0 -> searchNoteFragment.searchNotes(query)
            1 -> searchQuestionsFragment.searchQuestions(query)
            2 -> searchUserFragment.searchUsers(query)
        }
    }
}
