package com.example.studyfy.modules.bottomBar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.studyfy.R
import com.example.studyfy.modules.add.ui.AddNoteFragment
import com.example.studyfy.modules.add.ui.AddQuestionFragment
import com.example.studyfy.modules.explore.ExploreFragment
import com.example.studyfy.modules.home.HomeFragment
import com.example.studyfy.modules.library.LibraryFragment
import com.example.studyfy.modules.profile.ProfileFragment

class BottomBarController(private val fragmentManager: FragmentManager) {

    fun loadFragment(tab: Int) {
        if (tab == 2) {
            val dialog = AddBottomSheetDialog(
                onOptionSelected = {
                    val addQuestionFragment = AddQuestionFragment()
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, addQuestionFragment)
                        .commit()
                },
                onOption2Selected = {
                    val addNoteFragment = AddNoteFragment()
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, addNoteFragment)
                        .commit()
                }
            )
            dialog.show(fragmentManager, "AddBottomSheetDialog")
        } else {
            val fragment = getFragmentForTab(tab)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }


    private fun getFragmentForTab(tab: Int): Fragment {
        return when (tab) {
            0 -> HomeFragment()
            1 -> ExploreFragment()
            3 -> LibraryFragment()
            4 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}
