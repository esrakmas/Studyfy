package com.example.studyfy.modules.explore

import User
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyfy.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SearchUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()

    private val userList = mutableListOf<User>()
    private lateinit var adapter: UserAdapter
    private val currentUserId = "mevcut_kullanici_id" // Bunu auth ile çek

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = UserAdapter(currentUserId, userList) { userToFollow ->
            followUser(userToFollow)
        }
        recyclerView.adapter = adapter
        return view
    }

    private fun followUser(userToFollow: User) {
        val currentUserRef = firestore.collection("users").document(currentUserId)
        val userToFollowRef = firestore.collection("users").document(userToFollow.userId)

        firestore.runBatch { batch ->
            // currentUser'ın following listesine ekle
            batch.update(currentUserRef, "following", FieldValue.arrayUnion(userToFollow.userId))
            // userToFollow'un followers listesine ekle
            batch.update(userToFollowRef, "followers", FieldValue.arrayUnion(currentUserId))
        }.addOnSuccessListener {
            Toast.makeText(requireContext(), "${userToFollow.username} takip edildi", Toast.LENGTH_SHORT).show()
            // Güncel veriyi yeniden çek veya kullanıcı listesini güncelle
            searchUsers(lastQuery)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Takip işlemi başarısız", Toast.LENGTH_SHORT).show()
        }
    }

    private var lastQuery = ""

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
                    if (user.username.lowercase().contains(q)
                        || user.biography.lowercase().contains(q)
                        || user.email.lowercase().contains(q)
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
}
