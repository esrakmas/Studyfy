package com.example.studyfy.modules.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyfy.databinding.FragmentHomeBinding
import com.example.studyfy.modules.db.Post
import User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private val postList = mutableListOf<Post>()
    private lateinit var adapter: FollowingPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FollowingPostsAdapter(postList)
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.adapter = adapter

        currentUserId?.let {
            loadFollowingPosts(it)
        } ?: Toast.makeText(requireContext(), "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
    }

    private fun loadFollowingPosts(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (!isAdded || context == null) return@addOnSuccessListener // Fragment attach değilse çık

                val user = doc.toObject(User::class.java)
                val followingList = user?.following
                if (followingList.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Takip ettiğiniz kullanıcı yok", Toast.LENGTH_SHORT).show()
                    postList.clear()
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                val batches = followingList.chunked(10)
                postList.clear()

                for (batch in batches) {
                    firestore.collection("posts")
                        .whereIn("userId", batch)
                        .get()
                        .addOnSuccessListener { query ->
                            if (!isAdded || context == null) return@addOnSuccessListener

                            for (document in query.documents) {
                                val post = document.toObject(Post::class.java)
                                post?.let { postList.add(it) }
                            }
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            if (!isAdded || context == null) return@addOnFailureListener

                            Toast.makeText(requireContext(), "Gönderiler yüklenemedi", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                if (!isAdded || context == null) return@addOnFailureListener

                Toast.makeText(requireContext(), "Kullanıcı verisi alınamadı", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
