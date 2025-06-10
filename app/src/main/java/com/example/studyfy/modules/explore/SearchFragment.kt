package com.example.studyfy.modules.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyfy.R

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExplorerAdapter

    // Mock veri - Gerçek projede Firestore'dan vs. çekilecek
    private val allItems = listOf(
        ExplorerItem("Matematik", "Türev", "TYT", R.drawable.ic_launcher_background),
        ExplorerItem("Fizik", "Optik", "AYT", R.drawable.ic_launcher_background),
        ExplorerItem("Kimya", "Asit-Baz", "TYT", R.drawable.ic_launcher_background),
        ExplorerItem("Biyoloji", "Hücre", "AYT", R.drawable.ic_launcher_background),
        ExplorerItem("Tarih", "İnkılap", "KPSS", R.drawable.ic_launcher_background),
        ExplorerItem("Coğrafya", "İklim", "TYT", R.drawable.ic_launcher_background)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchView = view.findViewById(R.id.searchViewSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSuggestions)

        adapter = ExplorerAdapter(allItems.toMutableList())
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        searchView.isIconified = false
        searchView.requestFocus()

        // Klavyeyi aç (isteğe bağlı)
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)

        // Dinleme
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = allItems.filter {
                    it.subject.contains(newText.orEmpty(), ignoreCase = true) ||
                            it.topic.contains(newText.orEmpty(), ignoreCase = true)
                }
                adapter.updateList(filtered)
                return true
            }
        })

        return view
    }
}
