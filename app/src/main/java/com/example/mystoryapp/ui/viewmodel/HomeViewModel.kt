package com.example.mystoryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.mystoryapp.api.ApiConfig
import com.example.mystoryapp.datastores.UserSession
import com.example.mystoryapp.response.GetStoryResponse
import com.example.mystoryapp.response.ListStoryItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val pref: UserSession) : ViewModel() {
    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getToken():LiveData<String>{
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String){
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun getAllStories(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getAllStory("Bearer $token")
        client.enqueue(object :Callback<GetStoryResponse>{
            override fun onResponse(
                call: Call<GetStoryResponse>,
                response: Response<GetStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _listStories.value =response.body()?.listStory as List<ListStoryItem>?
                }
                else{
                    Log.e(TAG, "OnFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}