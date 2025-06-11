package com.example.studyfy.modules.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.studyfy.R

class SearchNoteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_note, container, false)
        val gridView = view.findViewById<GridView>(R.id.gridViewNotes)

        val notesList = listOf("Not 1", "Not 2", "Not 3", "Not 4", "Not 5")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notesList)
        gridView.adapter = adapter

        return view
    }
}
