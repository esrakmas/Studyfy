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
import com.google.firebase.firestore.FirebaseFirestore

class SearchUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()

    private var userList: MutableList<User> = mutableListOf()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter(userList)
        recyclerView.adapter = adapter
        return view
    }

    fun searchUsers(query: String) {
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

    class UserAdapter(private val users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<android.widget.TextView>(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return UserViewHolder(view)
        }

        override fun getItemCount(): Int = users.size

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.textView.text = "${user.username} - ${user.biography}"
        }
    }
}
