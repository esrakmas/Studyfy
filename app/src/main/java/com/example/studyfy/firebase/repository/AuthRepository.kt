package com.example.studyfy.firebase.repository

import com.example.studyfy.firebase.data.AuthState
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, callback: (AuthState) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(AuthState(true, auth.currentUser?.uid))
                } else {
                    callback(AuthState(false, errorMessage = task.exception?.message))
                }
            }
    }

    fun signUp(email: String, password: String, callback: (AuthState) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(AuthState(true, auth.currentUser?.uid))
                } else {
                    callback(AuthState(false, errorMessage = task.exception?.message))
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }
}
