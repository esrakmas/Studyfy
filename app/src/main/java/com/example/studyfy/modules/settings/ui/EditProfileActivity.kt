package com.example.studyfy.modules.settings.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.example.studyfy.R
import com.example.studyfy.modules.db.CloudinaryService
import com.example.studyfy.repository.UserRepository
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private var selectedImageUri: Uri? = null

    // Galeri'den görsel seçmek için launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(profileImage) // Seçilen fotoğrafı göster
            uploadImageToCloudinary(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profileImage = findViewById(R.id.profile_image)


        // Görsele tıklanırsa galeri aç
        profileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun uploadImageToCloudinary(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        CloudinaryService.uploadImage(tempFile.absolutePath) { imageUrl ->
            if (imageUrl != null) {
                Toast.makeText(this, "Fotoğraf yüklendi!", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(imageUrl).into(profileImage)

                // ✅ Firestore'a kaydet
                UserRepository.updateProfilePhotoUrl(imageUrl) { success ->
                    if (success) {
                        Toast.makeText(this, "Profil resmi güncellendi!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Profil resmi güncellenemedi!", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Yükleme başarısız!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
