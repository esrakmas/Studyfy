package com.example.studyfy.modules.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.databinding.ItemExplorerBinding
import com.example.studyfy.modules.db.Post

class PostGridFragmentAdapter(
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

        // Görsel yükleme
        Glide.with(context)
            .load(post.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.itemImage)

        // TextView'lara veri seti
        binding.tvSubject.text = post.subject ?: "Ders Yok"
        binding.tvType.text = post.type?.replaceFirstChar { it.uppercase() } ?: "Bilinmiyor"
        binding.tvLevel.text = post.topic ?: "Seviye Yok"

        // Tıklama - ViewModel aracılığı ile post'u set et ve fragment değiştir
        view.setOnClickListener {
            (context as? AppCompatActivity)?.let { activity ->
                val viewModel = ViewModelProvider(activity)[PostViewModel::class.java]
                viewModel.setPost(post)

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PostDetailFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }
}
