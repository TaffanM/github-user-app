package com.taffan.githubuser.data.retrofit

import android.util.Log
import com.taffan.githubuser.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = BuildConfig.BASE_URL
const val API_KEY = BuildConfig.API_KEY

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", API_KEY)
                    .build()
                chain.proceed(requestHeaders)
            }

            val rateLimitInterceptor = Interceptor { chain ->
                val response = chain.proceed(chain.request())
                val rateLimitMax = response.header("X-RateLimit-Limit")
                val rateLimitRemaining = response.header("X-RateLimit-Remaining")
                val rateLimitReset = response.header("X-RateLimit-Reset")

                if (rateLimitRemaining != null && rateLimitReset != null && rateLimitMax != null) {
                    val maxRequest = rateLimitMax.toInt()
                    val remainingRequests = rateLimitRemaining.toInt()
                    val resetTime = rateLimitReset.toLong()
                    Log.d("RateLimit", "Remaining requests: $remainingRequests, Reset time: $resetTime, Max : $maxRequest" )
                }

                response
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(rateLimitInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}