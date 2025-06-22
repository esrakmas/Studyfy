package com.example.studyfy.modules.post

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.FragmentPostDetailBinding
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.PostRepository
import com.example.studyfy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostViewModel
    private lateinit var currentPost: com.example.studyfy.modules.db.Post
    private val currentUserId: String? by lazy {
        FirebaseAuth.getInstance().currentUser?.uid
    }

    private var currentUsername: String = ""
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]

        commentAdapter = CommentAdapter(mutableListOf())
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        // Başta tüm yorum alanları gizli
        binding.commentsRecyclerView.visibility = View.GONE
        binding.commentInputContainer.visibility = View.GONE

        viewModel.selectedPost.observe(viewLifecycleOwner) { post ->
            post?.let {
                currentPost = it
                bindPost(it)
                setupLikeAndSaveButtons()
                setupCommentSection()
            }
        }

        binding.btnComment.setOnClickListener {
            toggleCommentSection()
        }
    }

    private fun toggleCommentSection() {
        val isVisible = binding.commentInputContainer.visibility == View.VISIBLE

        if (!isVisible) {
            // Göster
            binding.commentsRecyclerView.visibility = View.VISIBLE
            binding.commentInputContainer.visibility = View.VISIBLE
            binding.commentEditText.requestFocus()

            // Klavyeyi göster
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.commentEditText, InputMethodManager.SHOW_IMPLICIT)

            // ScrollView varsa EditText’e kaydır
            binding.root.post {
                (binding.root.parent as? NestedScrollView)?.smoothScrollTo(0, binding.commentEditText.bottom)
            }
        } else {
            // Gizle
            hideCommentSection()

        }
    }

    private fun hideCommentSection() {
        binding.commentsRecyclerView.visibility = View.GONE
        binding.commentInputContainer.visibility = View.GONE

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.commentEditText.windowToken, 0)
    }

    private fun bindPost(post: com.example.studyfy.modules.db.Post) {
        UserRepository.getUserById(post.userId) { user ->
            if (user != null) {
                currentUsername = user.username
                binding.username.text = user.username

                if (user.profileImageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
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

        Glide.with(requireContext()).load(post.imageUrl).into(binding.postImage)

        updateLikeButton(post.likes.contains(currentUserId))
        updateSaveButton(post.savedBy.contains(currentUserId))

        // ** Doğru cevabı göster **
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
            PostRepository.toggleLike(currentPost) { updatedPost ->
                updatedPost?.let {
                    currentPost = it
                    updateLikeButton(it.likes.contains(currentUserId))
                    binding.likeCount.text = it.likes.size.toString()
                }
            }
        }

        binding.btnSave.setOnClickListener {
            PostRepository.toggleSave(currentPost) { updatedPost ->
                updatedPost?.let {
                    currentPost = it
                    updateSaveButton(it.savedBy.contains(currentUserId))
                    binding.saveCount.text = it.savedBy.size.toString()
                }
            }
        }
    }

    private fun setupCommentSection() {
        binding.sendCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString().trim()

            if (commentText.isEmpty()) {
                Toast.makeText(requireContext(), "Yorum boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentUsername.isEmpty()) {
                Toast.makeText(requireContext(), "Kullanıcı adı yükleniyor, lütfen bekleyin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirestoreManager.addComment(
                currentPost.postId,
                commentText,
                currentUsername,
                onSuccess = {
                    binding.commentEditText.text.clear()
                    hideCommentSection()
                    loadComments(currentPost.postId)
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Yorum eklenemedi", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


    private fun loadComments(postId: String) {
        FirestoreManager.getComments(
            postId,
            onResult = { commentList ->
                commentAdapter.updateComments(commentList)
                binding.commentCount.text = commentList.size.toString()

                if (commentList.isNotEmpty()) {
                    binding.commentsRecyclerView.visibility = View.VISIBLE
                } else {
                    binding.commentsRecyclerView.visibility = View.GONE
                }
            },
            onFailure = {
                Toast.makeText(requireContext(), "Yorumlar alınamadı", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateLikeButton(isLiked: Boolean) {
        binding.btnLike.setImageResource(if (isLiked) R.drawable.ic_like2 else R.drawable.ic_like)
    }

    private fun updateSaveButton(isSaved: Boolean) {
        binding.btnSave.setImageResource(if (isSaved) R.drawable.ic_bookmark2 else R.drawable.ic_bookmark)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
