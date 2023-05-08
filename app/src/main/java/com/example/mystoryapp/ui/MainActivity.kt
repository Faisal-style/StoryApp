package com.example.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.datastores.UserSession
import com.example.mystoryapp.ui.fragment.LoginFragment
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.ui.home.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val pref by lazy { UserSession.getInstance(this.dataStore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        lifecycleScope.launch {
            val token = pref.getToken().first()
            if (token != null && token.isNotBlank()) {
                startHomeActivity()
            } else {
                // token kosong atau tidak ditemukan, tampilkan halaman login
                val fragment = LoginFragment.newInstance()
                addFragment(fragment)
            }
        }

    }
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    private fun addFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}