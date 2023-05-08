package com.example.mystoryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.datastores.UserSession

class ViewModelFactory(private val pref: UserSession): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return when{
           modelClass.isAssignableFrom(LoginViewModel::class.java) ->{
               LoginViewModel(pref) as T
           }
           modelClass.isAssignableFrom(HomeViewModel::class.java)-> {
               HomeViewModel(pref) as T
           }
           modelClass.isAssignableFrom(AddStoryViewModel::class.java)->{
               AddStoryViewModel(pref) as T
           }
           else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
       }
    }

}