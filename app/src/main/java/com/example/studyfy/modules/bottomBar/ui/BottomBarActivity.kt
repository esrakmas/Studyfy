package com.example.studyfy.modules.bottomBar.ui

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.studyfy.R
import com.example.studyfy.appcomponents.base.BaseActivity
import com.example.studyfy.databinding.ActivityBottomBarBinding
import com.example.studyfy.modules.add.AddFragment
import com.example.studyfy.modules.bottomBar.data.BottomBarVM
import com.example.studyfy.modules.explore.ui.ExploreFragment
import com.example.studyfy.modules.home.ui.HomeFragment
import com.example.studyfy.modules.library.ui.LibraryFragment
import com.example.studyfy.modules.profile.ui.ProfileFragment

class BottomBarActivity : BaseActivity<ActivityBottomBarBinding>(R.layout.activity_bottom_bar) {

    private val bottomBarVM: BottomBarVM by viewModels()

    override fun onInitialized() {
        binding.viewModel = bottomBarVM
        loadFragment(getFragmentForTab(bottomBarVM.bottomBarModel.value?.currentTab ?: 0))
    }

    override fun addObservers() {
        bottomBarVM.bottomBarModel.observe(this) { model ->
            loadFragment(getFragmentForTab(model.currentTab))
        }
    }

    override fun setUpClicks() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            bottomBarVM.setCurrentTab(
                when (item.itemId) {
                    R.id.navigation_home -> 0
                    R.id.navigation_explore -> 1
                    R.id.navigation_add -> 2
                    R.id.navigation_library -> 3
                    R.id.navigation_profile -> 4
                    else -> 0
                }
            )
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getFragmentForTab(tab: Int): Fragment {
        return when (tab) {
            0 -> HomeFragment()
            1 -> ExploreFragment()
            2 -> AddFragment()
            3 -> LibraryFragment()
            4 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}
