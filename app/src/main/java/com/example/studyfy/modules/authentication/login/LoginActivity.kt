package com.example.studyfy.modules.authentication.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyfy.databinding.ActivityLoginBinding
import com.example.studyfy.modules.authentication.signUp.SignUpActivity
import com.example.studyfy.modules.bottomBar.BottomBarActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, BottomBarActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Giriş başarısız: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Şifre sıfırlama özelliği eklenmedi", Toast.LENGTH_SHORT).show()
        }
    }
}
