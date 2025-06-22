package com.example.studyfy.modules.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.R
import com.example.studyfy.databinding.ActivitySettingsBinding
import com.example.studyfy.modules.authentication.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.studyfy.modules.db.FirestoreManager // FirestoreManager'ı import edin!

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.profileEdit.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.changePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Bildirimler açıldı.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bildirimler kapatıldı.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Çıkış Yap")
                .setMessage("Hesabınızdan çıkış yapmak istediğinizden emin misiniz?")
                .setPositiveButton("Evet") { _, _ ->
                    auth.signOut()
                    Toast.makeText(this, "Başarıyla çıkış yapıldı.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Hayır", null)
                .show()
        }

        binding.deleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hesabı Sil")
                .setMessage("Hesabınızı kalıcı olarak silmek istediğinizden emin misiniz? Bu işlem geri alınamaz!")
                .setPositiveButton("Evet, Sil") { _, _ ->
                    deleteUserAccount()
                }
                .setNegativeButton("İptal", null)
                .show()
        }
    }

    private fun deleteUserAccount() {
        val user = auth.currentUser
        user?.let { firebaseUser ->
            val userId = firebaseUser.uid

            // Önce Firebase Authentication'dan kullanıcıyı silmeye çalış
            firebaseUser.delete()
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Firebase Auth başarılı, şimdi Firestore verilerini sil
                        FirestoreManager.deleteUserData(
                            userId,
                            onSuccess = {
                                Toast.makeText(this, "Hesabınız başarıyla silindi.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            },
                            onFailure = { dbException ->
                                Toast.makeText(this, "Hesap silindi, ancak veritabanı silinirken hata oluştu: ${dbException.message}", Toast.LENGTH_LONG).show()
                                // Veritabanı silme işlemi kısmen başarısız olsa bile oturumu kapat ve giriş ekranına dön
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        )
                    } else {
                        Toast.makeText(this, "Hesap silinirken hata oluştu: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                        // Hata durumunda kullanıcıdan tekrar giriş yapmasını isteyebilirsiniz.
                        // Örneğin: if (authTask.exception is FirebaseAuthRecentLoginRequiredException) { ... }
                    }
                }
        } ?: run {
            Toast.makeText(this, "Silinecek bir kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}