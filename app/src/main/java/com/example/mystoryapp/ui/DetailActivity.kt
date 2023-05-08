package com.example.mystoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mystoryapp.databinding.ActivityDetailBinding
import com.example.mystoryapp.response.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding
    private lateinit var detailStories : ListStoryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        detailStories = intent.getParcelableExtra<ListStoryItem>(STORIES) as ListStoryItem
        binding.apply {
            tvNameDetail.text = detailStories.name
            tvDescDetail.text = detailStories.description
        }

        Glide.with(applicationContext)
            .load(detailStories.photoUrl)
            .into(binding.ivPhotoDetail)

    }

    companion object{
        const val STORIES = "stories"
    }
}