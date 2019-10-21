package com.github.sikv.photos.di.module

import com.github.sikv.photos.util.PEXELS
import com.github.sikv.photos.util.UNSPLASH
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class RetrofitModule {

    companion object {
        private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
        private const val PEXELS_BASE_URL = "https://api.pexels.com/v1/"
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
    }

    @Provides
    @Singleton
    @Named(UNSPLASH)
    fun provideUnsplashRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(UNSPLASH_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    @Named(PEXELS)
    fun providePexelsRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(PEXELS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}