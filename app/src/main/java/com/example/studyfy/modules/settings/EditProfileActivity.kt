package com.example.studyfy.modules.settings

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ActivityEditProfileBinding
import com.example.studyfy.modules.db.CloudinaryService
import com.example.studyfy.repository.UserRepository // UserRepository'yi import edin
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream // Dosya yazma için
import java.io.InputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth

    private var selectedImageUri: Uri? = null // Kullanıcının seçtiği yeni görselin URI'si
    private var currentProfileImageUrl: String = "" // Mevcut profil fotoğrafı URL'si (Firestore'dan çekilen)
    private var isNewImageSelected: Boolean = false // Yeni bir resim seçilip seçilmediğini takip et

    // Galeri'den görsel seçmek için launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it // Seçilen URI'yi kaydet
            isNewImageSelected = true // Yeni bir resim seçildi işaretle
            Glide.with(this).load(it).into(binding.profileImage) // Seçilen fotoğrafı hemen göster (ön izleme)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // ActionBar'da geri butonu ve başlık
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profili Düzenle"

        // Mevcut kullanıcı profil bilgilerini yükle
        loadUserProfile()

        // edit_pp (kalem ikonu) butonuna tıklayınca galeri aç
        binding.editPp.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Kaydet butonuna tıklama dinleyicisi
        binding.saveProfileChanges.setOnClickListener {
            saveProfileChanges()
        }
    }

    // Geri butonuna basıldığında bir önceki ekrana dön
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Kullanıcı oturumu bulunamadı.", Toast.LENGTH_SHORT).show()
            return
        }

        UserRepository.getUserById(userId) { user ->
            if (user != null) {
                // XML'deki ID'lere göre alanları doldur
                binding.editName.setText(user.username) // Kullanıcı adı olarak 'username' kullanıyoruz
                binding.editBio.setText(user.biography) // Biyografi
                // binding.editGradeLevel.setText(user.gradeLevel) // Eğer gradeLevel için bir TextInputEditText varsa bunu ekleyin
                currentProfileImageUrl = user.profileImageUrl // Mevcut URL'yi kaydet

                // Profil fotoğrafını yükle (varsa)
                if (user.profileImageUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.profileImageUrl)
                        .placeholder(R.drawable.ic_person) // Yer tutucu ikon
                        .error(R.drawable.ic_person) // Hata durumunda ikon
                        .circleCrop() // Dairesel profil fotoğrafı
                        .into(binding.profileImage)
                } else {
                    binding.profileImage.setImageResource(R.drawable.ic_person) // Varsayılan ikon
                }
            } else {
                Toast.makeText(this, "Kullanıcı verisi bulunamadı.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfileChanges() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Kullanıcı oturumu bulunamadı.", Toast.LENGTH_SHORT).show()
            return
        }

        // Kullanıcıdan alınan yeni değerler
        val newUsername = binding.editName.text.toString().trim() // XML'deki edit_name'i username için kullanıyoruz
        val newBiography = binding.editBio.text.toString().trim()
        // val newGradeLevel = binding.editGradeLevel.text.toString().trim() // Eğer gradeLevel için bir TextInputEditText varsa bunu ekleyin

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Kullanıcı adı boş olamaz.", Toast.LENGTH_SHORT).show()
            return
        }

        // Eğer yeni bir resim seçildiyse, önce Cloudinary'ye yükle
        if (isNewImageSelected && selectedImageUri != null) {
            uploadImageAndSaveProfile(userId, newUsername, newBiography, /*newGradeLevel*/ "", selectedImageUri!!) // Eğer gradeLevel yoksa boş string gönderdim
        } else {
            // Yeni resim seçilmediyse veya önceki resim URL'si korunacaksa
            saveProfileDataToFirestore(userId, newUsername, newBiography, /*newGradeLevel*/ "", currentProfileImageUrl) // Eğer gradeLevel yoksa boş string gönderdim
        }
    }

    private fun uploadImageAndSaveProfile(
        userId: String,
        username: String,
        biography: String,
        gradeLevel: String, // Eklendi
        uri: Uri
    ) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        // Geçici dosya oluştur
        val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        } ?: run {
            Toast.makeText(this, "Resim okunamadı.", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Profil fotoğrafı yükleniyor...", Toast.LENGTH_SHORT).show()

        CloudinaryService.uploadImage(tempFile.absolutePath) { imageUrl ->
            if (imageUrl != null) {
                // Cloudinary'ye yükleme başarılı, şimdi Firestore'a kaydet
                Toast.makeText(this, "Fotoğraf Cloudinary'ye yüklendi!", Toast.LENGTH_SHORT).show()
                saveProfileDataToFirestore(userId, username, biography, gradeLevel, imageUrl)
            } else {
                Toast.makeText(this, "Fotoğraf yükleme başarısız oldu.", Toast.LENGTH_SHORT).show()
                // Yükleme başarısız olsa bile diğer profil bilgilerini kaydetmeyi düşünebilirsin
                // saveProfileDataToFirestore(userId, username, biography, gradeLevel, currentProfileImageUrl)
            }
            tempFile.delete() // Geçici dosyayı sil
        }
    }

    private fun saveProfileDataToFirestore(
        userId: String,
        username: String,
        biography: String,
        gradeLevel: String, // Eklendi
        profileImageUrl: String
    ) {
        UserRepository.updateUserProfile(
            userId,
            username,
            biography,
            gradeLevel,
            profileImageUrl
        ) { success ->
            if (success) {
                Toast.makeText(this@EditProfileActivity, "Profil başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                finish() // Activity'yi kapat
            } else {
                Toast.makeText(this@EditProfileActivity, "Profil güncellenirken hata oluştu.", Toast.LENGTH_LONG).show()
            }
        }
    }
}