package com.example.studyfy.modules.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.studyfy.R
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.post.PostGridAdapter
import com.google.firebase.firestore.FirebaseFirestore

class SearchQuestionsFragment : Fragment() {

    private lateinit var gridView: GridView
    private lateinit var adapter: PostGridAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val questionList = mutableListOf<Post>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_questions, container, false)
        gridView = view.findViewById(R.id.gridViewQuestions)
        adapter = PostGridAdapter(requireContext(), questionList)
        gridView.adapter = adapter
        return view
    }

    fun searchQuestions(query: String) {
        if (query.isBlank()) {
            questionList.clear()
            adapter.notifyDataSetChanged()
            return
        }

        firestore.collection("posts")
            .whereEqualTo("type", "question")
            .get()
            .addOnSuccessListener { documents ->
                questionList.clear()
                val q = query.lowercase()
                for (doc in documents) {
                    val post = doc.toObject(Post::class.java)
                    if (post.description.lowercase().contains(q)
                        || post.subject.lowercase().contains(q)
                        || post.topic.lowercase().contains(q)
                    ) {
                        questionList.add(post)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Arama sırasında hata oluştu", Toast.LENGTH_SHORT).show()
            }
    }
}
