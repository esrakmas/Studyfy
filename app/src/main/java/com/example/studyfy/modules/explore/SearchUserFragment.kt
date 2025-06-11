package com.example.studyfy.modules.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyfy.R

class SearchUserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewUsers)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = UserAdapter(listOf("Kullanıcı 1", "Kullanıcı 2", "Kullanıcı 3"))

        return view
    }

    class UserAdapter(private val users: List<String>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

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
            holder.textView.text = users[position]
        }
    }
}
