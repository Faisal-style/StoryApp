package com.example.mystoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mystoryapp.datastores.UserSession

class AddStoryViewModel(private val pref : UserSession): ViewModel() {
    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
}