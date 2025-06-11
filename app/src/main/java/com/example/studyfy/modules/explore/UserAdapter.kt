package com.example.studyfy.modules.explore

import User
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ItemSuggestionUserBinding

class UserAdapter(
    private val currentUserId: String,
    private val users: MutableList<User>,
    private val onFollowClicked: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemSuggestionUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemSuggestionUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            textUsername.text = user.username
            textBiography.text = user.biography

            Glide.with(holder.binding.root.context)
                .load(user.profileImageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.binding.imageProfile)


            // Takip durumunu kontrol et
            val isFollowed = user.followers.contains(currentUserId)
            buttonFollow.visibility = if (user.userId == currentUserId || isFollowed) View.GONE else View.VISIBLE

            buttonFollow.setOnClickListener {
                onFollowClicked(user)
            }
        }
    }
}
