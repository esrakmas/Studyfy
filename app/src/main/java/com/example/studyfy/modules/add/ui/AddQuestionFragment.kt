package com.example.studyfy.modules.add.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.FragmentAddQuestionBinding
import com.example.studyfy.modules.db.CloudinaryService
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.db.UriUtils
import com.google.firebase.auth.FirebaseAuth

class AddQuestionFragment : Fragment() {

    private var _binding: FragmentAddQuestionBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var correctAnswer: String? = null

    // Görsel seçmek için launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(binding.imgQuestion)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spinner için veriler
        val classList = listOf("9. Sınıf", "10. Sınıf", "11. Sınıf", "12. Sınıf")
        val subjectList = listOf("Matematik", "Fizik", "Kimya", "Biyoloji", "Tarih")

        val classAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classList)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerClass.adapter = classAdapter

        val subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectList)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubject.adapter = subjectAdapter

        // Görsel seçmek için tıklama
        binding.imageView3.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        // Kameradan çekmek için istersen ekle

        // Doğru cevabı seçmek için butonlar
        val answerButtons = listOf(
            binding.btnAnswerA,
            binding.btnAnswerB,
            binding.btnAnswerC,
            binding.btnAnswerD,
            binding.btnAnswerE
        )

        answerButtons.forEach { button ->
            button.setOnClickListener {
                // Tüm butonların arkaplanını resetle
                answerButtons.forEach { btn -> btn.setBackgroundResource(R.drawable.button_background_green) }
                // Tıklanan butonu seçili yap (örnek: renk değiştir)
                button.setBackgroundResource(R.drawable.button_background_selected) // Bu drawable'ı sen eklemelisin
                correctAnswer = button.text.toString()
            }
        }

        // Gönder butonu tıklaması
        binding.btnSubmit.setOnClickListener {
            uploadQuestion()
        }
    }

    private fun uploadQuestion() {
        val selectedClass = binding.spinnerClass.selectedItem?.toString() ?: ""
        val selectedSubject = binding.spinnerSubject.selectedItem?.toString() ?: ""
        val description = binding.etDescription.text.toString().trim()
        val isPrivate = binding.switch1.isChecked

        if (selectedClass.isEmpty() || selectedSubject.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        if (correctAnswer.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Lütfen doğru cevabı seçin", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false

        // Kullanıcı ID al (FirebaseAuth)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (selectedImageUri != null) {
            val imagePath = UriUtils.getPathFromUri(requireContext(), selectedImageUri!!)
            if (imagePath != null) {
                CloudinaryService.uploadImage(imagePath) { imageUrl ->
                    if (imageUrl != null) {
                        saveQuestionToFirestore(
                            selectedClass, selectedSubject, description,
                            imageUrl, isPrivate, userId, correctAnswer!!
                        )
                    } else {
                        binding.btnSubmit.isEnabled = true
                        Toast.makeText(requireContext(), "Resim yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.btnSubmit.isEnabled = true
                Toast.makeText(requireContext(), "Resim yolu alınamadı", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Görsel yoksa da kaydet
            saveQuestionToFirestore(
                selectedClass, selectedSubject, description,
                "", isPrivate, userId, correctAnswer!!
            )
        }
    }

    private fun saveQuestionToFirestore(
        selectedClass: String,
        selectedSubject: String,
        description: String,
        imageUrl: String,
        isPrivate: Boolean,
        userId: String,
        correctAnswer: String
    ) {
        val post = Post(
            postId = "", // FirestoreManager'da atanacak
            userId = userId,
            type = "question",
            subject = selectedSubject,
            topic = selectedClass,
            description = description,
            imageUrl = imageUrl,
            isPrivate = isPrivate,
            likes = emptyList(),
            savedBy = emptyList(),
            commentsCount = 0,
            createdAt = null,
            correctAnswer = correctAnswer
        )

        FirestoreManager.uploadPost(post, {
            binding.btnSubmit.isEnabled = true
            Toast.makeText(requireContext(), "Soru başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
            clearInputs()
        }, { error ->
            binding.btnSubmit.isEnabled = true
            Toast.makeText(requireContext(), "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun clearInputs() {
        binding.spinnerClass.setSelection(0)
        binding.spinnerSubject.setSelection(0)
        binding.etDescription.text?.clear()
        binding.switch1.isChecked = false
        selectedImageUri = null
        correctAnswer = null
        binding.imgQuestion.setImageResource(R.drawable.ic_launcher_background)

        // Buton arka planlarını resetle
        val answerButtons = listOf(
            binding.btnAnswerA,
            binding.btnAnswerB,
            binding.btnAnswerC,
            binding.btnAnswerD,
            binding.btnAnswerE
        )
        answerButtons.forEach { it.setBackgroundResource(R.drawable.button_background_green) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
