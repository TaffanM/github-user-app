package com.taffan.githubuser.ui.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taffan.githubuser.data.response.GithubResponse
import com.taffan.githubuser.data.response.ItemsItem
import com.taffan.githubuser.data.retrofit.ApiConfig
import com.taffan.githubuser.preferences.SettingPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SettingPreferences): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listItems = MutableLiveData<List<ItemsItem>>()
    val listItems: LiveData<List<ItemsItem>> = _listItems

    init {
        findUserGitHub("Taffan")
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

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

}