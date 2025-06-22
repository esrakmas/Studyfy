package com.example.studyfy.modules.explore

import User
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ActivityUserProfileBinding
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.post.PostGridActivityAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var viewedUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewedUserId = intent.getStringExtra("userId") ?: run {
            Toast.makeText(this, "Kullanıcı ID alınamadı", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserProfile(viewedUserId)
        loadUserPosts(viewedUserId)

        binding.followButton.setOnClickListener {
            toggleFollowStatus()
        }
    }

    private fun loadUserProfile(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        binding.username.text = "@${user.username}"
                        binding.bio.text = user.biography.ifBlank { "Henüz biyografi yok" }
                        binding.followersCount.text = user.followers.size.toString()
                        binding.followingCount.text = user.following.size.toString()

                        Glide.with(this)
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .circleCrop()
                            .into(binding.profileImage)

                        // Takip durumu butonu güncelle
                        updateFollowButton(user.followers)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Profil yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFollowButton(followers: List<String>) {
        if (currentUserId == viewedUserId) {
            binding.followButton.text = "Bu benim profilim"
            binding.followButton.isEnabled = false
        } else if (followers.contains(currentUserId)) {
            binding.followButton.text = "Takipten Çık"
        } else {
            binding.followButton.text = "Takip Et"
        }
    }

    private fun toggleFollowStatus() {
        if (currentUserId == null || currentUserId == viewedUserId) return

        val userDoc = firestore.collection("users").document(viewedUserId)
        val currentUserDoc = firestore.collection("users").document(currentUserId!!)

        firestore.runTransaction { transaction ->
            val viewedUserSnapshot = transaction.get(userDoc)
            val currentUserSnapshot = transaction.get(currentUserDoc)

            val viewedUser = viewedUserSnapshot.toObject(User::class.java)
            val currentUser = currentUserSnapshot.toObject(User::class.java)

            if (viewedUser != null && currentUser != null) {
                val newFollowers = viewedUser.followers.toMutableList()
                val newFollowing = currentUser.following.toMutableList()

                if (newFollowers.contains(currentUserId)) {
                    newFollowers.remove(currentUserId)
                    newFollowing.remove(viewedUserId)
                } else {
                    newFollowers.add(currentUserId!!)
                    newFollowing.add(viewedUserId)
                }

                transaction.set(userDoc, viewedUser)
                transaction.set(currentUserDoc, currentUser)
            }
        }.addOnSuccessListener {
            loadUserProfile(viewedUserId) // Takip sayısı ve butonu yenile
        }.addOnFailureListener {
            Toast.makeText(this, "Takip işlemi başarısız", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserPosts(userId: String) {
        firestore.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val postList = snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
                binding.postsCount.text = postList.size.toString()
                val adapter = PostGridActivityAdapter(this, postList)
                binding.postsGrid.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gönderiler alınamadı", Toast.LENGTH_SHORT).show()
            }
    }
}
