package com.example.studyfy.modules.explore

import User
import android.content.Intent
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
    private val onFollowToggle: (User, Boolean) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemSuggestionUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemSuggestionUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val binding = holder.binding

        binding.textUsername.text = user.username
        binding.textBiography.text = user.biography

        Glide.with(binding.root.context)
            .load(user.profileImageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imageProfile)

        val isFollowed = user.followers.contains(currentUserId)

        if (user.userId == currentUserId) {
            binding.buttonFollow.visibility = View.GONE
        } else {
            binding.buttonFollow.visibility = View.VISIBLE
            binding.buttonFollow.text = if (isFollowed) "Takip Ediliyor" else "Takip Et"

            binding.buttonFollow.setOnClickListener {
                onFollowToggle(user, isFollowed)
            }
        }

        // Kullanıcının profiline gitmek için layout'a tıklama
        binding.userLayout.setOnClickListener {
            val context = binding.root.context
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("userId", user.userId)
            context.startActivity(intent)
        }
    }
}
