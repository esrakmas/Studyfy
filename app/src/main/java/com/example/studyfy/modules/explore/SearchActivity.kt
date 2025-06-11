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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SearchView odaklama ve klavye açma
        binding.searchView2.isIconified = false
        binding.searchView2.requestFocus()

        val imm = getSystemService<InputMethodManager>()
        imm?.showSoftInput(binding.searchView2.findFocus(), InputMethodManager.SHOW_IMPLICIT)

        // ViewPager2 adapteri ayarla
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> SearchNoteFragment()
                1 -> SearchQuestionsFragment()
                2 -> SearchUserFragment()
                else -> throw IllegalStateException("Pozisyon bulunamadı")
            }
        }

        // TabLayout ve ViewPager2'yi bağla
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // SearchView listener ekle (isteğe bağlı)
        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Arama yapıldığında işlemler buraya
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Yazı değiştiğinde fragmentlere arama sorgusu iletilebilir
                return true
            }
        })
    }
}
