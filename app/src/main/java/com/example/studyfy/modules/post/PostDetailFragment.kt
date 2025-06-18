package com.example.studyfy.modules.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.FragmentPostDetailBinding
import com.example.studyfy.modules.db.PostRepository
import com.example.studyfy.modules.post.PostViewModel
import com.example.studyfy.repository.UserRepository

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostViewModel
    private lateinit var currentPost: com.example.studyfy.modules.db.Post
    private val currentUserId: String? by lazy {
        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    }

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

        viewModel.selectedPost.observe(viewLifecycleOwner) { post ->
            if (post != null) {
                currentPost = post
                bindPost(post)

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
                            binding.shareCount.text = it.savedBy.size.toString()
                        }
                    }
                }
            }
        }
    }

    private fun bindPost(post: com.example.studyfy.modules.db.Post) {
        // Kullanıcı bilgilerini userId ile çek
        UserRepository.getUserById(post.userId) { user ->
            if (user != null) {
                // Kullanıcı adı
                binding.username.text = user.username

                // Profil fotoğrafı varsa yükle, yoksa default göster
                if (user.profileImageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(user.profileImageUrl)
                        .circleCrop()
                        .into(binding.profileImage)
                } else {
                    binding.profileImage.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else {
                // Kullanıcı bulunamazsa fallback
                binding.username.text = "Bilinmeyen Kullanıcı"
                binding.profileImage.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }

        // Gönderi açıklaması
        binding.postDescription.text = post.description

        // Beğeni sayısı
        binding.likeCount.text = post.likes.size.toString()

        // Yorum sayısı
        binding.commentCount.text = post.commentsCount.toString()

        // Kaydetme sayısı
        binding.shareCount.text = post.savedBy.size.toString()

        // Konu ve seviye
        binding.postSubject.text = post.subject
        binding.postLevel.text = post.topic

        // Gönderi türü (Not/Soru)
        binding.postType.text = if (post.type == "note") "Not" else "Soru"

        // Gönderi resmi
        Glide.with(requireContext())
            .load(post.imageUrl)
            .into(binding.postImage)

        // Butonların durumunu güncelle
        updateLikeButton(post.likes.contains(currentUserId))
        updateSaveButton(post.savedBy.contains(currentUserId))
    }

    private fun updateLikeButton(isLiked: Boolean) {
        val icon = if (isLiked) R.drawable.ic_like2 else R.drawable.ic_like
        binding.btnLike.setImageResource(icon)
    }

    private fun updateSaveButton(isSaved: Boolean) {
        val icon = if (isSaved) R.drawable.ic_bookmark2 else R.drawable.ic_bookmark
        binding.btnSave.setImageResource(icon)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
