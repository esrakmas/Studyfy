package com.example.studyfy.modules.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studyfy.R

class ExplorerAdapter(private var items: List<ExplorerItem>) :
    RecyclerView.Adapter<ExplorerAdapter.ExplorerViewHolder>() {

    inner class ExplorerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.itemImage)
        val subject: TextView = view.findViewById(R.id.tvSubject)
        val topic: TextView = view.findViewById(R.id.tvType)
        val level: TextView = view.findViewById(R.id.tvLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExplorerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_explorer, parent, false)
        return ExplorerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExplorerViewHolder, position: Int) {
        val item = items[position]
        holder.subject.text = item.subject
        holder.topic.text = item.topic
        holder.level.text = item.level
        holder.image.setImageResource(item.imageRes)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<ExplorerItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
