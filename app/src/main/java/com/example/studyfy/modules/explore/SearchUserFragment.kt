package com.example.studyfy.modules.explore

import User
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyfy.databinding.FragmentSearchUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SearchUserFragment : Fragment() {

    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId get() = auth.currentUser?.uid ?: ""

    private val userList = mutableListOf<User>()
    private lateinit var adapter: UserAdapter

    private var lastQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)

        adapter = UserAdapter(currentUserId, userList) { user, isFollowed ->
            toggleFollow(user, isFollowed)
        }

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = adapter

        return binding.root
    }

    fun searchUsers(query: String) {
        lastQuery = query
        if (query.isBlank()) {
            userList.clear()
            adapter.notifyDataSetChanged()
            return
        }

        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                userList.clear()
                val q = query.lowercase()
                for (doc in documents) {
                    val user = doc.toObject(User::class.java)
                    if ((user.username.lowercase().contains(q) ||
                                user.biography.lowercase().contains(q) ||
                                user.email.lowercase().contains(q)) &&
                        user.userId != currentUserId
                    ) {
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Kullanıcı aramada hata oluştu", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleFollow(user: User, isFollowed: Boolean) {
        val currentUserRef = firestore.collection("users").document(currentUserId)
        val userToToggleRef = firestore.collection("users").document(user.userId)

        firestore.runBatch { batch ->
            if (isFollowed) {
                // Takipten çık
                batch.update(currentUserRef, "following", FieldValue.arrayRemove(user.userId))
                batch.update(userToToggleRef, "followers", FieldValue.arrayRemove(currentUserId))
            } else {
                // Takip et
                batch.update(currentUserRef, "following", FieldValue.arrayUnion(user.userId))
                batch.update(userToToggleRef, "followers", FieldValue.arrayUnion(currentUserId))
            }
        }.addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                if (isFollowed) "${user.username} takipten çıkarıldı" else "${user.username} takip edildi",
                Toast.LENGTH_SHORT
            ).show()
            searchUsers(lastQuery)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "İşlem başarısız", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
