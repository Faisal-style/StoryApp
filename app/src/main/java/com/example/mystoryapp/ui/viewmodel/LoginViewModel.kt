package com.example.mystoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.datastores.UserSession
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserSession) : ViewModel() {
    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }
}