package com.example.studyfy.firebase.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyfy.firebase.repository.StorageRepository

class StorageViewModel(private val repository: StorageRepository) : ViewModel() {
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    fun uploadImage(fileUri: Uri) {
        repository.uploadImage(fileUri) { url ->
            _imageUrl.value = url
        }
    }
}
