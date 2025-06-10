package com.example.studyfy.modules.db


import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}