package com.example.studyfy.modules.post

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var viewModel: PostViewModel
    private var currentPost: com.example.studyfy.modules.db.Post? = null
    private val currentUserId: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    private lateinit var commentAdapter: CommentAdapter
    private var currentUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel'i doğru şekilde başlat
        viewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // Yorum bölümünü başlangıçta gizle
        binding.commentsRecyclerView.visibility = View.GONE
        binding.commentInputContainer.visibility = View.GONE

        commentAdapter = CommentAdapter(mutableListOf())
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.commentsRecyclerView.adapter = commentAdapter

        viewModel.selectedPost.observe(this) { post ->
            if (post == null) {
                Toast.makeText(this, "Gönderi verisi alınamadı", Toast.LENGTH_SHORT).show()
                finish()
                return@observe
            }
            currentPost = post
            bindPost(post)
            setupLikeAndSaveButtons()
            setupCommentSection()
        }

        binding.btnComment.setOnClickListener {
            toggleCommentSection()
        }
    }

    private fun toggleCommentSection() {
        if (isFinishing || isDestroyed) return // Aktivite kapanıyorsa işlem yapma

        val isVisible = binding.commentInputContainer.visibility == View.VISIBLE

        if (!isVisible) {
            // Yorum bölümünü göster
            binding.commentsRecyclerView.visibility = View.VISIBLE
            binding.commentInputContainer.visibility = View.VISIBLE
            binding.commentEditText.requestFocus()

            // Klavyeyi göster
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.commentEditText, InputMethodManager.SHOW_IMPLICIT)

            // EditText'e otomatik kaydır
            binding.root.post {
                if (isFinishing || isDestroyed) return@post
                (binding.root.parent as? NestedScrollView)?.smoothScrollTo(0, binding.commentEditText.bottom)
            }
        } else {
            // Yorum bölümünü gizle
            hideCommentSection()
        }
    }

    private fun hideCommentSection() {
        if (isFinishing || isDestroyed) return

        binding.commentsRecyclerView.visibility = View.GONE
        binding.commentInputContainer.visibility = View.GONE

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.commentEditText.windowToken, 0)
    }

    private fun bindPost(post: com.example.studyfy.modules.db.Post) {
        UserRepository.getUserById(post.userId) { user ->
            if (isFinishing || isDestroyed) return@getUserById // Aktivite kapanıyorsa işlem yapma

            if (user != null) {
                currentUsername = user.username
                binding.username.text = user.username

                if (user.profileImageUrl.isNotEmpty()) {
                    Glide.with(this@PostDetailActivity)
                        .load(user.profileImageUrl)
                        .circleCrop()
                        .into(binding.profileImage)
                } else {
                    binding.profileImage.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else {
                binding.username.text = "Bilinmeyen Kullanıcı"
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

        updateLikeButton(post.likes.contains(currentUserId))
        updateSaveButton(post.savedBy.contains(currentUserId))

        // Doğru cevap gösterimi
        if (!post.correctAnswer.isNullOrEmpty()) {
            binding.postCorrectAnswer.visibility = View.VISIBLE
            binding.postCorrectAnswer.text = "Doğru Cevap: ${post.correctAnswer}"
        } else {
            binding.postCorrectAnswer.visibility = View.GONE
        }

        loadComments(post.postId)
    }

    private fun setupLikeAndSaveButtons() {
        binding.btnLike.setOnClickListener {
            currentPost?.let { post ->
                PostRepository.toggleLike(post) { updatedPost ->
                    if (isFinishing || isDestroyed) return@toggleLike // Aktivite kapanıyorsa işlem yapma

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
                    if (isFinishing || isDestroyed) return@toggleSave // Aktivite kapanıyorsa işlem yapma

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
            if (isFinishing || isDestroyed) return@setOnClickListener // Aktivite kapanıyorsa işlem yapma

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
                        if (isFinishing || isDestroyed) return@addComment
                        binding.commentEditText.text.clear()
                        hideCommentSection()
                        loadComments(post.postId)
                    },
                    onFailure = {
                        if (isFinishing || isDestroyed) return@addComment
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
                if (isFinishing || isDestroyed) return@getComments // Aktivite kapanıyorsa işlem yapma

                commentAdapter.updateComments(commentList)
                binding.commentCount.text = commentList.size.toString()

                if (commentList.isNotEmpty()) {
                    binding.commentsRecyclerView.visibility = View.VISIBLE
                } else {
                    binding.commentsRecyclerView.visibility = View.GONE
                }
            },
            onFailure = {
                if (isFinishing || isDestroyed) return@getComments
                Toast.makeText(this, "Yorumlar alınamadı", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateLikeButton(isLiked: Boolean) {
        binding.btnLike.setImageResource(if (isLiked) R.drawable.ic_like2 else R.drawable.ic_like)
    }

    private fun updateSaveButton(isSaved: Boolean) {
        binding.btnSave.setImageResource(if (isSaved) R.drawable.ic_bookmark2 else R.drawable.ic_bookmark)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Tüm pending callbacks'leri temizle
        viewModel.selectedPost.removeObservers(this)
    }
}