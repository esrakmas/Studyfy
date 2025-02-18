package com.example.studyfy.firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyfy.firebase.repository.AuthRepository
import com.example.studyfy.firebase.data.AuthState

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    fun signIn(email: String, password: String) {
        repository.signIn(email, password) { state ->
            _authState.value = state
        }
    }

    fun signUp(email: String, password: String) {
        repository.signUp(email, password) { state ->
            _authState.value = state
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState(false)
    }
}
