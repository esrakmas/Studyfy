package com.example.studyfy.modules.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyfy.modules.db.Post

class PostViewModel : ViewModel() {

    private val _selectedPost = MutableLiveData<Post>()
    val selectedPost: LiveData<Post> get() = _selectedPost

    fun setPost(post: Post) {
        _selectedPost.value = post
    }
}