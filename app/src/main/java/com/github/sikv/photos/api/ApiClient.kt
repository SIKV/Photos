package com.github.sikv.photos.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor() {

    companion object {
        private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
        private const val PEXELS_BASE_URL = "https://api.pexels.com/v1/"

        val INSTANCE: ApiClient by lazy { ApiClient() }
    }

    val unsplashClient: UnsplashClient
    val pexelsClient: PexelsClient

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        val unsplashRetrofit = Retrofit.Builder()
                .baseUrl(UNSPLASH_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        unsplashClient = UnsplashClient(unsplashRetrofit.create(UnsplashApi::class.java))

        val pexelsRetrofit = Retrofit.Builder()
                .baseUrl(PEXELS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        pexelsClient = PexelsClient(pexelsRetrofit.create(PexelsApi::class.java))
    }
}