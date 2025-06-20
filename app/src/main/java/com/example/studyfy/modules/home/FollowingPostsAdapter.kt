package com.example.studyfy.modules.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ItemPostBinding
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.db.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class FollowingPostsAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<FollowingPostsAdapter.PostViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.binding.root.context

        with(holder.binding) {
            postType.text = post.type.capitalize()
            postSubject.text = post.subject
            postLevel.text = post.topic ?: "Bilinmiyor"
            postDescription.text = post.description
            likeCount.text = post.likes.size.toString()
            commentCount.text = post.commentsCount.toString()
            saveCount.text = post.savedBy.size.toString()

            // Görsel
            Glide.with(postImage.context)
                .load(post.imageUrl)
                .placeholder(android.R.color.darker_gray)
                .into(postImage)

            // Kullanıcı bilgileri
            firestore.collection("users").document(post.userId)
                .get()
                .addOnSuccessListener { document ->
                    val usernameText = document.getString("username") ?: "Anonim"
                    val profileImageUrl = document.getString("profileImageUrl") ?: ""

                    username.text = usernameText
                    if (profileImageUrl.isNotEmpty()) {
                        Glide.with(profileImage.context)
                            .load(profileImageUrl)
                            .circleCrop()
                            .into(profileImage)
                    } else {
                        profileImage.setImageResource(android.R.drawable.sym_def_app_icon)
                    }
                }

            // 🔁 Beğen durumu
            val isLiked = post.likes.contains(currentUserId)
            btnLike.setImageResource(if (isLiked) R.drawable.ic_like2 else R.drawable.ic_like)

            btnLike.setOnClickListener {
                PostRepository.toggleLike(post) { updatedPost ->
                    updatedPost?.let {
                        posts[position] = it
                        notifyItemChanged(position)
                    }
                }
            }

            // 🔁 Kaydet durumu
            val isSaved = post.savedBy.contains(currentUserId)
            btnSave.setImageResource(if (isSaved) R.drawable.ic_bookmark2 else R.drawable.ic_bookmark)

            btnSave.setOnClickListener {
                PostRepository.toggleSave(post) { updatedPost ->
                    updatedPost?.let {
                        posts[position] = it
                        notifyItemChanged(position)
                    }
                }
            }

            // 🔁 Yorum butonu
            btnComment.setOnClickListener {
                // Burada yorum ekranına yönlendirme yapılabilir (örn: yorumları gösteren Fragment açmak)
                Toast.makeText(context, "Yorum özelliği burada devreye girer.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = posts.size
}
