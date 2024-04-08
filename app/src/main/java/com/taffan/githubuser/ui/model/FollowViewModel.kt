package com.taffan.githubuser.ui.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taffan.githubuser.data.response.ItemsItem
import com.taffan.githubuser.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel: ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _followerList = MutableLiveData<List<ItemsItem>>()
    val followerList: LiveData<List<ItemsItem>> = _followerList

    private val _followingList = MutableLiveData<List<ItemsItem>>()
    val followingList: LiveData<List<ItemsItem>> = _followingList

    fun findFollowerUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowers(username)
        client.enqueue(object: Callback<List<ItemsItem>>{
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    val followerResponse= response.body()
                    _followerList.value = followerResponse
                } else {
                    Log.e("responseError", "onFailureResponseFollow: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e("responseError", "onFailure: ${t.message}")
            }

        })
    }

    fun findFollowingUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowing(username)
        client.enqueue(object: Callback<List<ItemsItem>>{
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    val followingResponse= response.body()
                    _followingList.value = followingResponse
                } else {
                    Log.e("responseError", "onFailureResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e("responseError", "onFailure: ${t.message}")
            }

        })
    }
}