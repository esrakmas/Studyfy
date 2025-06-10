package com.example.studyfy.modules.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.studyfy.R
import com.example.studyfy.modules.settings.ui.SettingsActivity
import com.example.studyfy.repository.UserRepository

class ProfileFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvBio: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView
    private lateinit var postsCount: TextView
    private lateinit var settingsIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI referansları bağla
        tvUsername = view.findViewById(R.id.username)
        tvBio = view.findViewById(R.id.bio)
        followersCount = view.findViewById(R.id.followers_count)
        followingCount = view.findViewById(R.id.following_count)
        postsCount = view.findViewById(R.id.posts_count)
        settingsIcon = view.findViewById(R.id.settings_icon)

        // Ayarlar ikonuna tıklanınca
        settingsIcon.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
            // SettingsActivity'ye geçiş yapmak
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Her seferinde kullanıcı verisini yeniden çek
        UserRepository.getCurrentUser { user ->
            if (user != null) {
                tvUsername.text = "@${user.username}"
                tvBio.text = user.biography.ifBlank { "Henüz biyografi eklenmedi" }
                followersCount.text = user.followers.size.toString()
                followingCount.text = user.following.size.toString()

                // Post sayısı kısmı (ileride güncellenecek)
                postsCount.text = "0"
            } else {
                Toast.makeText(requireContext(), "Kullanıcı verisi alınamadı", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
