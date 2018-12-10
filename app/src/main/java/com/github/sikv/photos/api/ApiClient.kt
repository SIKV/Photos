package com.github.sikv.photos.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor() {

    companion object {
        private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"

        val INSTANCE: ApiClient by lazy { ApiClient() }
    }

    val unsplashClient: UnsplashClient

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(UNSPLASH_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        unsplashClient = UnsplashClient(retrofit.create(UnsplashApi::class.java))
    }
}