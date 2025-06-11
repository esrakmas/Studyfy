package com.example.studyfy.modules.profile

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.modules.db.Post
import android.view.LayoutInflater
import android.widget.BaseAdapter
import com.example.studyfy.databinding.ItemPostGridBinding

class PostGridAdapter(
    private val context: Context,
    private val postList: List<Post>
) : BaseAdapter() {

    override fun getCount(): Int = postList.size

    override fun getItem(position: Int): Any = postList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemPostGridBinding
        val view: View

        if (convertView == null) {
            binding = ItemPostGridBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemPostGridBinding
        }

        val post = postList[position]
        Glide.with(context)
            .load(post.imageUrl)
            .into(binding.imagePost)

        return view
    }
}
