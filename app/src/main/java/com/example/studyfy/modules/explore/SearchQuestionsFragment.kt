package com.example.studyfy.modules.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.studyfy.R

class SearchQuestionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_questions, container, false)
        val gridView = view.findViewById<GridView>(R.id.gridViewQuestions)

        val questionsList = listOf("Soru 1", "Soru 2", "Soru 3", "Soru 4")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, questionsList)
        gridView.adapter = adapter

        return view
    }
}
