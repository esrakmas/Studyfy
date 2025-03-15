package com.example.studyfy.modules.bottomBar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.studyfy.R
import com.example.studyfy.modules.add.ui.AddFragment
import com.example.studyfy.modules.explore.ui.ExploreFragment
import com.example.studyfy.modules.home.ui.HomeFragment
import com.example.studyfy.modules.library.ui.LibraryFragment
import com.example.studyfy.modules.profile.ui.ProfileFragment

class BottomBarController(private val fragmentManager: FragmentManager) {

    fun loadFragment(tab: Int) {
        val fragment = getFragmentForTab(tab)
        fragmentManager.beginTransaction()
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
