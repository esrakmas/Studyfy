package com.example.studyfy.firebase.data

data class AuthState(
    val isAuthenticated: Boolean,
    val userId: String? = null,
    val errorMessage: String? = null
)
