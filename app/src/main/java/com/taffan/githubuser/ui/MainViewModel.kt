package com.taffan.githubuser.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.taffan.githubuser.data.response.GithubResponse
import com.taffan.githubuser.data.response.ItemsItem
import com.taffan.githubuser.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listItems = MutableLiveData<List<ItemsItem>>()
    val listItems: LiveData<List<ItemsItem>> = _listItems

    init {
        findUserGitHub("github")
    }

    fun findUserGitHub(searchText: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().findUser(searchText)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    val items = response.body()?.items
                    _listItems.value = items

                } else {
                    Log.e("responseError", "onFailureResponse: ${response.message()}")

                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("responseError", "onFailure: ${t.message}")
            }

        })

    }
}