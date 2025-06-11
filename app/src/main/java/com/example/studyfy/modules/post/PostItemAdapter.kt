package com.example.studyfy.modules.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyfy.R
import com.example.studyfy.modules.db.Post

class PostItemAdapter(
    private val items: List<Post>
) : RecyclerView.Adapter<PostItemAdapter.PostViewHolder>() {

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val tvSubject: TextView = view.findViewById(R.id.tvSubject)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvLevel: TextView = view.findViewById(R.id.tvLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_explorer, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = items[position]
        // Glide ile imageUrl varsa yükle, yoksa placeholder göster
        if (post.imageUrl.isNullOrEmpty()) {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background)
        } else {
            Glide.with(holder.imageView.context)
                .load(post.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView)
        }

        holder.tvSubject.text = post.subject
        holder.tvType.text = post.type.capitalize()
        holder.tvLevel.text = post.topic // veya post.level varsa onu koyabilirsin
    }

    override fun getItemCount(): Int = items.size
}
