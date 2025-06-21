package com.example.studyfy.modules.post

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ActivityPostDetailBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.PostRepository
import com.example.studyfy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private val viewModel: PostViewModel by viewModels() // ViewModel instance
    private var currentPost: com.example.studyfy.modules.db.Post? = null
    private val currentUserId: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    private lateinit var commentAdapter: CommentAdapter
    private var currentUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commentAdapter = CommentAdapter(mutableListOf())
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentsRecyclerView.adapter = commentAdapter

        // ViewModel'deki seçili Post'u gözlemle
        viewModel.selectedPost.observe(this, Observer { post ->
            if (post == null) {
                Toast.makeText(this, "Gönderi verisi alınamadı", Toast.LENGTH_SHORT).show()
                finish()
                return@Observer
            }
            currentPost = post
            bindPost(post)
            setupLikeAndSaveButtons()
            setupCommentSection()
        })

        binding.btnComment.setOnClickListener {
            toggleCommentSection()
        }
    }

    private fun bindPost(post: com.example.studyfy.modules.db.Post) {
        UserRepository.getUserById(post.userId) { user ->
            currentUsername = user?.username ?: ""
            binding.username.text = user?.username ?: "Bilinmeyen Kullanıcı"

            if (!user?.profileImageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(user?.profileImageUrl)
                    .circleCrop()
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }

        binding.postDescription.text = post.description
        binding.likeCount.text = post.likes.size.toString()
        binding.commentCount.text = post.commentsCount.toString()
        binding.saveCount.text = post.savedBy.size.toString()
        binding.postSubject.text = post.subject
        binding.postLevel.text = post.topic
        binding.postType.text = if (post.type == "note") "Not" else "Soru"

        Glide.with(this).load(post.imageUrl).into(binding.postImage)

        if (!post.correctAnswer.isNullOrEmpty()) {
            binding.postCorrectAnswer.visibility = android.view.View.VISIBLE
            binding.postCorrectAnswer.text = "Doğru Cevap: ${post.correctAnswer}"
        } else {
            binding.postCorrectAnswer.visibility = android.view.View.GONE
        }

        updateLikeButton(post.likes.contains(currentUserId))
        updateSaveButton(post.savedBy.contains(currentUserId))

        loadComments(post.postId)
    }

    private fun setupLikeAndSaveButtons() {
        binding.btnLike.setOnClickListener {
            currentPost?.let { post ->
                PostRepository.toggleLike(post) { updatedPost ->
                    updatedPost?.let {
                        currentPost = it
                        updateLikeButton(it.likes.contains(currentUserId))
                        binding.likeCount.text = it.likes.size.toString()
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            currentPost?.let { post ->
                PostRepository.toggleSave(post) { updatedPost ->
                    updatedPost?.let {
                        currentPost = it
                        updateSaveButton(it.savedBy.contains(currentUserId))
                        binding.saveCount.text = it.savedBy.size.toString()
                    }
                }
            }
        }
    }

    private fun setupCommentSection() {
        binding.sendCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString().trim()
            if (commentText.isEmpty()) {
                Toast.makeText(this, "Yorum boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentUsername.isEmpty()) {
                Toast.makeText(this, "Kullanıcı adı yükleniyor, lütfen bekleyin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentPost?.let { post ->
                FirestoreManager.addComment(
                    post.postId,
                    commentText,
                    currentUsername,
                    onSuccess = {
                        binding.commentEditText.text.clear()
                        hideCommentSection()
                        loadComments(post.postId)
                    },
                    onFailure = {
                        Toast.makeText(this, "Yorum eklenemedi", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun loadComments(postId: String) {
        FirestoreManager.getComments(
            postId,
            onResult = { commentList ->
                commentAdapter.updateComments(commentList)
                binding.commentCount.text = commentList.size.toString()

                binding.commentsRecyclerView.visibility =
                    if (commentList.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            },
            onFailure = {
                Toast.makeText(this, "Yorumlar alınamadı", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun toggleCommentSection() {
        val isVisible = binding.commentInputContainer.visibility == android.view.View.VISIBLE
        if (!isVisible) {
            binding.commentsRecyclerView.visibility = android.view.View.VISIBLE
            binding.commentInputContainer.visibility = android.view.View.VISIBLE
            binding.commentEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.commentEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            hideCommentSection()
        }
    }

    private fun hideCommentSection() {
        binding.commentsRecyclerView.visibility = android.view.View.GONE
        binding.commentInputContainer.visibility = android.view.View.GONE
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.commentEditText.windowToken, 0)
    }

    private fun updateLikeButton(isLiked: Boolean) {
        binding.btnLike.setImageResource(if (isLiked) R.drawable.ic_like2 else R.drawable.ic_like)
    }

    private fun updateSaveButton(isSaved: Boolean) {
        binding.btnSave.setImageResource(if (isSaved) R.drawable.ic_bookmark2 else R.drawable.ic_bookmark)
    }
}
