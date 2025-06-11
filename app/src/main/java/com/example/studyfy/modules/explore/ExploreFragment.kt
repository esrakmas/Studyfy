package com.example.studyfy.modules.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.example.studyfy.R
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.post.PostGridAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ExploreFragment : Fragment() {

    private lateinit var gridView: GridView
    private lateinit var postAdapter: PostGridAdapter
    private val postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        gridView = view.findViewById(R.id.posts_grid_exp) // ID GridView de olsa olabilir
        postAdapter = PostGridAdapter(requireContext(), postList)
        gridView.adapter = postAdapter

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
