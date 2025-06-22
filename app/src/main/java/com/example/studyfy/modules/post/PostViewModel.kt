// 2. PostViewModel.kt dosyasında:
package com.example.studyfy.modules.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyfy.modules.db.Post
import com.example.studyfy.modules.db.PostRepository // Import ettiğinizden emin olun

class PostViewModel : ViewModel() {

    private val _selectedPost = MutableLiveData<Post?>() // Nullable yapıldı
    val selectedPost: LiveData<Post?> = _selectedPost // Getter düzeltildi

    // YENİ EKLENECEK METOT:
    fun loadPostById(postId: String) {
        PostRepository.getPostById(postId) { post ->
            _selectedPost.value = post
        }
    }

    // Mevcut setPost metodunuzu isterseniz bırakabilirsiniz, ancak artık loadPostById ana yöntem olacak.
    fun setPost(post: Post) {
        _selectedPost.value = post
    }
}