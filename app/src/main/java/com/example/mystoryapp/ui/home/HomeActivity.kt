package com.example.mystoryapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.ActivityHomeBinding
import com.example.mystoryapp.datastores.UserSession
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.ui.AddStoryActivity
import com.example.mystoryapp.ui.MainActivity
import com.example.mystoryapp.ui.fragment.LoginFragment
import com.example.mystoryapp.ui.viewmodel.HomeViewModel
import com.example.mystoryapp.ui.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_token")

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var arrayListStories: ArrayList<ListStoryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserSession.getInstance(dataStore)

        homeViewModel = ViewModelProvider(this, ViewModelFactory(pref))[HomeViewModel::class.java]
        homeViewModel.getToken().observe(this) { token: String ->
            if (token.isEmpty()) {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
            } else {
                homeViewModel.getAllStories(token)
            }
        }
        homeViewModel.listStories.observe(this) { listStory ->
            showRecyclerView(listStory)
        }

        homeViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        val layoutManager = LinearLayoutManager(this)
        binding.rvHome.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvHome.addItemDecoration(itemDecoration)
    }

    private fun showRecyclerView(listStories: List<ListStoryItem>) {
        binding.apply {
            rvHome.apply {
                rvHome.setHasFixedSize(true)
                arrayListStories = ArrayList()
                arrayListStories.addAll(listStories)
                val listStoryAdapter = ListStoryAdapter(arrayListStories)
                rvHome.adapter = listStoryAdapter
            }
        }
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarLayoutRv.visibility = View.VISIBLE
            binding.progressbarRv.visibility = View.VISIBLE

        } else {
            binding.progressBarLayoutRv.visibility = View.INVISIBLE
            binding.progressbarRv.visibility = View.INVISIBLE

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                homeViewModel.saveToken("")
                Snackbar.make(binding.root, "Logged out successfully!", Snackbar.LENGTH_LONG).show()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                true
            }
            R.id.addStory -> {
                val i = Intent(this, AddStoryActivity::class.java)
                startActivity(i)
                true
            }
            else -> true
        }
    }
}