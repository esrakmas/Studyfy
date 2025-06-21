package com.example.studyfy.modules.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.studyfy.databinding.FragmentProfileBinding
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.post.PostGridFragmentAdapter
import com.example.studyfy.modules.settings.ui.SettingsActivity
import com.example.studyfy.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsIcon.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        UserRepository.getCurrentUser { user ->
            if (user != null) {
                binding.username.text = "@${user.username}"
                binding.bio.text = user.biography.ifBlank { "Henüz biyografi eklenmedi" }
                binding.followersCount.text = user.followers.size.toString()
                binding.followingCount.text = user.following.size.toString()

                // 🔥 Profil fotoğrafı yükle
                if (user.profileImageUrl.isNotBlank()) {
                    Glide.with(requireContext())
                        .load(user.profileImageUrl)
                        .placeholder(com.example.studyfy.R.drawable.ic_launcher_background)
                        .into(binding.profileImage)
                }

                // 🔥 Kullanıcının postlarını Firestore'dan al ve GridView'a yerleştir
                FirebaseFirestore.getInstance()
                    .collection("posts")
                    .whereEqualTo("userId", user.userId)
                    .get()
                    .addOnSuccessListener { result ->
                        val postList = result.documents.mapNotNull { it.toObject(Post::class.java) }
                        binding.postsCount.text = postList.size.toString()
                        val adapter = PostGridFragmentAdapter(requireContext(), postList)
                        binding.postsGrid.adapter = adapter
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Postlar alınamadı", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(requireContext(), "Kullanıcı verisi alınamadı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
