package com.example.studyfy.firebase.repository

import com.example.studyfy.firebase.data.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addUser(user: UserModel, callback: (Boolean) -> Unit) {
        db.collection("users").document(user.userId)
            .set(user)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getUser(userId: String, callback: (UserModel?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
    }
}
