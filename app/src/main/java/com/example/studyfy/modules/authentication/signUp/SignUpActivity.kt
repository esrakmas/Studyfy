package com.example.studyfy.modules.authentication.signUp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.databinding.ActivitySignUpBinding
import com.example.studyfy.modules.authentication.login.LoginActivity
import com.example.studyfy.modules.bottomBar.BottomBarActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Alanların boş olmaması kontrolü
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // E-posta format kontrolü
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Geçerli bir e-posta giriniz"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            // Şifre eşleşme kontrolü
            if (password != confirmPassword) {
                Toast.makeText(this, "Şifreler uyuşmuyor!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Şifre uzunluk kontrolü (örnek: minimum 6 karakter)
            if (password.length < 6) {
                binding.etPassword.error = "Şifre en az 6 karakter olmalı"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            // Firebase Authentication ile kullanıcı oluşturma
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val userId = auth.currentUser?.uid ?: return@addOnSuccessListener

                    val user = hashMapOf(
                        "userId" to userId,
                        "fullName" to fullName,
                        "username" to username,
                        "email" to email,
                        "biography" to "",
                        "gradeLevel" to "11",
                        "followers" to listOf<String>(),
                        "following" to listOf<String>(),

                    )


                    // Firestore'a kullanıcı verisini ekle
                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, BottomBarActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Veritabanı hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    // Hata mesajlarına özel kontrol
                    val message = when {
                        e.message?.contains("email address is already in use", ignoreCase = true) == true ->
                            "Bu e-posta zaten kullanılıyor."
                        e.message?.contains("The email address is badly formatted", ignoreCase = true) == true ->
                            "Geçersiz e-posta formatı."
                        else -> "Kayıt başarısız: ${e.message}"
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
