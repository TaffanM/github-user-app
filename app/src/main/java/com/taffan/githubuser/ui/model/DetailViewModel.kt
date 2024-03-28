package com.taffan.githubuser.ui.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taffan.githubuser.data.response.DetailUserResponse
import com.taffan.githubuser.data.retrofit.ApiConfig
import com.taffan.githubuser.database.FavoriteUser
import com.taffan.githubuser.repository.FavoriteUserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(application: Application): ViewModel() {
    private val mFavoriteUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    fun getAllUser(): LiveData<List<FavoriteUser>> = mFavoriteUserRepository.getAllUser()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _followers = MutableLiveData<Int>()
    val followers: LiveData<Int> = _followers

    private val _following = MutableLiveData<Int>()
    val following: LiveData<Int> = _following

    private val _htmlUrl = MutableLiveData<String>()
    val htmlUrl: LiveData<String> = _htmlUrl

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name


    fun findDetailedInfo(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailUser(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    val userResponse = response.body()
                    val followers = userResponse?.followers
                    _followers.value = followers
                    val following = userResponse?.following
                    _following.value = following
                    val name = userResponse?.name
                    _name.value = name
                    val htmlUrl = userResponse?.htmlUrl
                    _htmlUrl.value = htmlUrl

                } else {
                    Log.e("responseError", "onFailureResponseDetail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("responseError", "onFailure: ${t.message}")
            }

        })
    }
}