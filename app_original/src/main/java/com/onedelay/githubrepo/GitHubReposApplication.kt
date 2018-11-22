package com.onedelay.githubrepo

import android.app.Application
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class GitHubReposApplication() : Application() {
    lateinit var retrofit: Retrofit
    lateinit var gitHubService: GitHubService

    override fun onCreate() {
        super.onCreate()
        // 어느 액티비티에서나 API 를 이용할 수 있도록 이 클래스에서 구현
        setupAPIClient()
    }

    private fun setupAPIClient() {
        val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.d("API LOG", it)
        })

        logging.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        gitHubService = retrofit.create(GitHubService::class.java)
    }
}