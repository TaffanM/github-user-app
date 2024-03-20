package com.taffan.githubuser.data.retrofit

import com.taffan.githubuser.BuildConfig
import com.taffan.githubuser.data.response.DetailUserResponse
import com.taffan.githubuser.data.response.GithubResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("/search/users")
    fun findUser(
        @Query("q") username: String
    ): Call<GithubResponse>

    @GET("users/{username}")
    fun getDetailUser(@Path("username") username: String): Call<DetailUserResponse>
}