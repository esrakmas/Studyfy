package com.example.studyfy.modules.bottomBar

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R
import com.example.studyfy.databinding.ActivityBottomBarBinding

class BottomBarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomBarBinding
    private val bottomBarVM: BottomBarVM by viewModels()
    private lateinit var bottomBarController: BottomBarController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomBarController başlatılıyor
        bottomBarController = BottomBarController(supportFragmentManager)

        // Varsayılan sekmeyi yükle
        bottomBarController.loadFragment(bottomBarVM.getCurrentTab())

        setupBottomNavigation()
        observeViewModel()
    }

    // Bottom Navigation tıklamalarını dinler
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val selectedTab = when (item.itemId) {
                R.id.navigation_home -> 0
                R.id.navigation_explore -> 1
                R.id.navigation_add -> 2
                R.id.navigation_library -> 3
                R.id.navigation_profile -> 4
                else -> 0
            }
            bottomBarVM.setCurrentTab(selectedTab)
            true
        }
    }

    // ViewModel değişimlerini dinler ve fragment değiştirir
    private fun observeViewModel() {
        bottomBarVM.currentTab.observe(this) { tab ->
            bottomBarController.loadFragment(tab)
        }
    }
}
