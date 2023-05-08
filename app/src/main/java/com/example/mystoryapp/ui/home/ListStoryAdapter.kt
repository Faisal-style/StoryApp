package com.example.mystoryapp.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.R
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.ui.DetailActivity


class ListStoryAdapter(private val listStory: ArrayList<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgStory : ImageView = itemView.findViewById(R.id.img_story)
        private val name : TextView = itemView.findViewById(R.id.txt_name_home)

        fun bind(getAllStoryData: ListStoryItem){
            name.text = getAllStoryData.name
            Glide.with(itemView.context)
                .load(getAllStoryData.photoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgStory)
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(STORIES, getAllStoryData)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_story, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val result = listStory[position]
        holder.bind(result)
    }

    companion object{
        const val STORIES = "stories"
    }


}