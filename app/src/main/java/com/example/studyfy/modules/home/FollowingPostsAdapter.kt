package com.example.studyfy.modules.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ItemPostBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.db.PostRepository
import com.example.studyfy.modules.post.CommentAdapter
import com.example.studyfy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FollowingPostsAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<FollowingPostsAdapter.PostViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val commentAdapter = CommentAdapter(mutableListOf())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.binding.root.context

        with(holder.binding) {
            postType.text = if (post.type == "note") "Not" else "Soru"
            postSubject.text = post.subject
            postLevel.text = post.topic ?: "Bilinmiyor"
            postDescription.text = post.description
            likeCount.text = post.likes.size.toString()
            commentCount.text = post.commentsCount.toString()
            saveCount.text = post.savedBy.size.toString()

            // 📌 Doğru Cevap (soruysa göster)
            if (post.type == "question" && !post.correctAnswer.isNullOrEmpty()) {
                postCorrectAnswer.visibility = View.VISIBLE
                postCorrectAnswer.text = "Doğru Cevap: ${post.correctAnswer}"
            } else {
                postCorrectAnswer.visibility = View.GONE
            }

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

            // 🔁 Yorum alanı başta gizli
            commentsRecyclerView.layoutManager = LinearLayoutManager(context)
            commentsRecyclerView.adapter = holder.commentAdapter
            commentsRecyclerView.visibility = View.GONE
            commentInputContainer.visibility = View.GONE

            // 🔁 Yorumları aç/kapat
            btnComment.setOnClickListener {
                val isVisible = commentInputContainer.visibility == View.VISIBLE

                if (!isVisible) {
                    commentsRecyclerView.visibility = View.VISIBLE
                    commentInputContainer.visibility = View.VISIBLE
                    commentEditText.requestFocus()
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT)
                    loadComments(post.postId, holder)
                } else {
                    commentsRecyclerView.visibility = View.GONE
                    commentInputContainer.visibility = View.GONE
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(commentEditText.windowToken, 0)
                }
            }

            // 🔁 Yorum gönder
            sendCommentButton.setOnClickListener {
                val commentText = commentEditText.text.toString().trim()

                if (commentText.isEmpty()) {
                    Toast.makeText(context, "Yorum boş olamaz", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                UserRepository.getCurrentUsername { username ->
                    if (username.isNullOrEmpty()) {
                        Toast.makeText(context, "Kullanıcı adı alınamadı", Toast.LENGTH_SHORT)
                            .show()
                        return@getCurrentUsername
                    }

                    FirestoreManager.addComment(
                        post.postId,
                        commentText,
                        username,
                        onSuccess = {
                            commentEditText.text.clear()
                            loadComments(post.postId, holder)

                            // Yorum alanını kapat
                            holder.binding.commentsRecyclerView.visibility = View.GONE
                            holder.binding.commentInputContainer.visibility = View.GONE

                            // Klavyeyi kapat
                            val imm =
                                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(
                                holder.binding.commentEditText.windowToken,
                                0
                            )
                        },
                        onFailure = {
                            Toast.makeText(context, "Yorum eklenemedi", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

        }
    }

    private fun loadComments(postId: String, holder: PostViewHolder) {
        FirestoreManager.getComments(
            postId,
            onResult = { comments ->
                holder.commentAdapter.updateComments(comments)
                holder.binding.commentCount.text = comments.size.toString()
            },
            onFailure = {
                Toast.makeText(
                    holder.binding.root.context,
                    "Yorumlar alınamadı",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    override fun getItemCount(): Int = posts.size
}
