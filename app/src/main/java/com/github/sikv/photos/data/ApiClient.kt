package com.github.sikv.photos.data

import com.github.sikv.photos.api.PhotosApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor() {

    companion object {
        const val BASE_URL = "https://api.unsplash.com/"

        val INSTANCE: ApiClient by lazy { ApiClient() }
    }

    val photosClient: PhotosClient

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        photosClient = PhotosClient(retrofit.create(PhotosApi::class.java))
    }
}