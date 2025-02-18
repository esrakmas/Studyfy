package com.example.studyfy.firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyfy.firebase.repository.UserRepository
import com.example.studyfy.firebase.data.UserModel

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    fun addUser(user: UserModel) {
        repository.addUser(user) { success ->
            if (success) {
                _user.value = user
            }
        }
    }

    fun getUser(userId: String) {
        repository.getUser(userId) { userData ->
            _user.value = userData
        }
    }
}
