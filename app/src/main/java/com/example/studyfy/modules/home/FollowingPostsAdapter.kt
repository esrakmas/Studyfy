package com.example.studyfy.modules.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyfy.databinding.ItemPostBinding
import com.example.studyfy.modules.db.Post
import com.google.firebase.firestore.FirebaseFirestore

class FollowingPostsAdapter(private val posts: List<Post>) :
    RecyclerView.Adapter<FollowingPostsAdapter.PostViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.binding.root.context

        // Post verilerini bağla
        with(holder.binding) {
            postType.text = post.type.capitalize()
            postSubject.text = post.subject
            postLevel.text = post.topic ?: "Bilinmiyor"
            postDescription.text = post.description
            likeCount.text = post.likes.size.toString()
            commentCount.text = post.commentsCount.toString()
            saveCount.text = post.savedBy.size.toString()

            // Post resmi
            Glide.with(postImage.context)
                .load(post.imageUrl)
                .placeholder(android.R.color.darker_gray)
                .into(postImage)

            // Kullanıcı bilgisi için firestore'dan kullanıcıyı çek
            firestore.collection("users").document(post.userId)
                .get()
                .addOnSuccessListener { document ->
                    if(document.exists()) {
                        val username = document.getString("username") ?: "Anonim"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        username?.let {
                            this.username.text = it
                        }
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(profileImage.context)
                                .load(profileImageUrl)
                                .placeholder(android.R.color.darker_gray)
                                .circleCrop()
                                .into(profileImage)
                        } else {
                            profileImage.setImageResource(android.R.drawable.sym_def_app_icon)
                        }
                    }
                }
                .addOnFailureListener {
                    // Hata durumunda varsayılan değerleri kullan
                    username.text = "Anonim"
                    profileImage.setImageResource(android.R.drawable.sym_def_app_icon)
                }
        }
    }

    override fun getItemCount(): Int = posts.size
}
