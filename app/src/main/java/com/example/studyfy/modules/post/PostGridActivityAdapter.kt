package com.example.studyfy.modules.post

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ItemExplorerBinding
import com.example.studyfy.modules.db.Post

class PostGridActivityAdapter(
    private val context: Context,
    private val postList: List<Post>
) : BaseAdapter() {

    override fun getCount(): Int = postList.size

    override fun getItem(position: Int): Any = postList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemExplorerBinding
        val view: View

        if (convertView == null) {
            binding = ItemExplorerBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemExplorerBinding
        }

        val post = postList[position]

        // Görsel ve metinler
        Glide.with(context)
            .load(post.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.itemImage)

        binding.tvSubject.text = post.subject ?: "Ders Yok"
        binding.tvType.text = post.type?.replaceFirstChar { it.uppercase() } ?: "Bilinmiyor"
        binding.tvLevel.text = post.topic ?: "Seviye Yok"

        // Tıklayınca: Detay activity'sine gönder
        view.setOnClickListener {
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra("postId", post.postId)  // PostDetailActivity Firestore'dan çekmeli
            context.startActivity(intent)
        }

        return view
    }
}
