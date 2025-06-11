package com.example.studyfy.modules.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.studyfy.databinding.FragmentPostDetailBinding
import com.example.studyfy.modules.post.PostViewModel

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostViewModel

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
                // Kullanıcı adı
                binding.username.text = post.userId

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

                // Resmi yükle
                Glide.with(requireContext())
                    .load(post.imageUrl)
                    .into(binding.postImage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
