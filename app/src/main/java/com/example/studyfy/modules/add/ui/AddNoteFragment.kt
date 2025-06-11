package com.example.studyfy.modules.add.ui

import android.R
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.studyfy.databinding.FragmentAddNoteBinding
import com.example.studyfy.modules.db.CloudinaryService
import com.example.studyfy.modules.db.FirestoreManager
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.db.UriUtils

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivNoteImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val classList = listOf("9. Sınıf", "10. Sınıf", "11. Sınıf", "12. Sınıf")
        val subjectList = listOf("Matematik", "Fizik", "Kimya", "Biyoloji", "Tarih")

        // Adapter oluştur ve spinner'a bağla
        val classAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, classList)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectClass.adapter = classAdapter

        val subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectList)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectSubject.adapter = subjectAdapter


        binding.ivNoteImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSubmitNote.setOnClickListener {
            uploadNote()
        }
    }

    private fun uploadNote() {
        val selectedClass = binding.spinnerSelectClass.selectedItem?.toString() ?: ""
        val selectedSubject = binding.spinnerSelectSubject.selectedItem?.toString() ?: ""
        val description = binding.etNoteDescription.text.toString().trim()
        val isPrivate = binding.switchPrivacy.isChecked

        if (selectedClass.isEmpty() || selectedSubject.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmitNote.isEnabled = false

        if (selectedImageUri != null) {
            val imagePath = UriUtils.getPathFromUri(requireContext(), selectedImageUri!!)
            if (imagePath != null) {
                CloudinaryService.uploadImage(imagePath) { imageUrl ->
                    if (imageUrl != null) {
                        saveNoteToFirestore(selectedClass, selectedSubject, description, imageUrl, isPrivate)
                    } else {
                        binding.btnSubmitNote.isEnabled = true
                        Toast.makeText(requireContext(), "Resim yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.btnSubmitNote.isEnabled = true
                Toast.makeText(requireContext(), "Resim yolu alınamadı", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveNoteToFirestore(
        selectedClass: String,
        selectedSubject: String,
        description: String,
        imageUrl: String,
        isPrivate: Boolean
    ) {
        val post = Post(
            type = "note",
            subject = selectedSubject,
            topic = selectedClass,
            description = description,
            imageUrl = imageUrl,
            isPrivate = isPrivate,
            userId = "",
            likes = emptyList(),
            savedBy = emptyList(),
            commentsCount = 0,
            createdAt = null
        )

        FirestoreManager.uploadPost(post, {
            binding.btnSubmitNote.isEnabled = true
            Toast.makeText(requireContext(), "Not başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
            clearInputs()
        }, { error ->
            binding.btnSubmitNote.isEnabled = true
            Toast.makeText(requireContext(), "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun clearInputs() {
        binding.spinnerSelectClass.setSelection(0)
        binding.spinnerSelectSubject.setSelection(0)
        binding.etNoteDescription.text?.clear()
        binding.switchPrivacy.isChecked = false
        binding.ivNoteImage.setImageResource(com.example.studyfy.R.drawable.ic_launcher_background)
        selectedImageUri = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ✅ URI -> String (gerçek dosya yolu) çevirici
    private fun getRealPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = requireContext().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
}
