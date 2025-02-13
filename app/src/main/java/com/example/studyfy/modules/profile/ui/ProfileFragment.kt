package com.example.studyfy.modules.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.studyfy.R

import com.example.studyfy.modules.explore.ui.ExploreFragment
import com.example.studyfy.modules.settings.ui.SettingsActivity


class ProfileFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsIcon: ImageView = view.findViewById(R.id.settings_icon)

        settingsIcon.setOnClickListener {
            // SettingsActivity'ye geçiş yapmak
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

}