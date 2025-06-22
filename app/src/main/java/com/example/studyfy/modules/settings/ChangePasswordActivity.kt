package com.example.studyfy.modules.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.databinding.ActivityChangePasswordBinding // View Binding için gerekli
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.EmailAuthProvider // Kimlik doğrulama için gerekli
import com.example.studyfy.repository.UserRepository // UserRepository'yi import edin

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding'i başlat
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // ActionBar'da geri butonu ve başlık
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Şifre Değiştir"

        // Kaydet butonuna tıklama dinleyicisi
        binding.savePasswordChanges.setOnClickListener {
            changePassword()
        }
    }

    // Geri butonuna basıldığında bir önceki ekrana dön
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun changePassword() {
        // XML'den gelen yeni ID'leri kullanıyoruz
        val currentPassword = binding.editCurrentPassword.text.toString().trim()
        val newPassword = binding.editNewPassword.text.toString().trim()
        val confirmNewPassword = binding.editConfirmNewPassword.text.toString().trim()

        // Alanların boş olup olmadığını kontrol et
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            return
        }

        // Yeni şifre uzunluğunu kontrol et
        if (newPassword.length < 6) {
            Toast.makeText(this, "Yeni şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show()
            return
        }

        // Yeni şifrelerin eşleşip eşleşmediğini kontrol et
        if (newPassword != confirmNewPassword) {
            Toast.makeText(this, "Yeni şifreler eşleşmiyor.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Kullanıcı bulunamadı, lütfen tekrar giriş yapın.", Toast.LENGTH_SHORT).show()
            return
        }

        // Kullanıcının kimliğini mevcut şifresiyle yeniden doğrula
        // Güvenlik için bu adım zorunludur.
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // Kimlik doğrulama başarılı, şimdi şifreyi güncelle
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Şifreniz başarıyla değiştirildi!", Toast.LENGTH_SHORT).show()
                                finish() // Şifre değiştirme ekranını kapat
                            } else {
                                // Şifre güncelleme başarısız olursa
                                Toast.makeText(this, "Şifre değiştirilemedi: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    // Kimlik doğrulama başarısız olursa
                    if (reauthTask.exception is FirebaseAuthRecentLoginRequiredException) {
                        Toast.makeText(this, "Güvenlik nedeniyle, lütfen tekrar giriş yapın ve şifrenizi değiştirmeyi deneyin.", Toast.LENGTH_LONG).show()
                        // Burada kullanıcıyı tekrar giriş ekranına yönlendirmek iyi bir UX olabilir
                        // val intent = Intent(this, LoginActivity::class.java)
                        // startActivity(intent)
                        // finish()
                    } else {
                        Toast.makeText(this, "Mevcut şifreniz yanlış veya bir hata oluştu: ${reauthTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}