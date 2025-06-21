package com.example.studyfy.modules.explore

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.example.studyfy.R
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.post.PostGridFragmentAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ExploreFragment : Fragment() {

    private lateinit var gridView: GridView
    private lateinit var postAdapter: PostGridFragmentAdapter
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        gridView = view.findViewById(R.id.posts_grid_exp)
        postAdapter = PostGridFragmentAdapter(requireContext(), postList)
        gridView.adapter = postAdapter

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView1)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
                searchView.clearFocus() // Kullanıcı klavyeye tıklamadan önce ekranı temizle
            }
        }

        fetchPostsFromFirestore()
        return view
    }

    private fun fetchPostsFromFirestore() {
        FirebaseFirestore.getInstance()
            .collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                postList.clear()
                for (doc in snapshot.documents) {
                    val post = doc.toObject(Post::class.java)
                    post?.let { postList.add(it) }
                }
                postAdapter.notifyDataSetChanged()
            }
    }
}
