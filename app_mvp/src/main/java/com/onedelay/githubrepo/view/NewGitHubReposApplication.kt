package com.onedelay.githubrepo.view

import android.app.Application
import android.util.Log
import com.onedelay.githubrepo.model.GitHubService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NewGitHubReposApplication : Application() {
    lateinit var gitHubService: GitHubService

    override fun onCreate() {
        super.onCreate()
        // 어느 Activity 에서나 API 를 이용할 수 있도록
        setupAPIClient()
    }

    private fun setupAPIClient() {
        val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Log.d("API LOG", message) })

        logging.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        gitHubService = retrofit.create(GitHubService::class.java)
    }
}