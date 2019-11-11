package com.github.sikv.photos.di.module

import com.github.sikv.photos.api.PexelsApi
import com.github.sikv.photos.api.UnsplashApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class ApiModule {

    @Provides
    fun provideUnsplashApi(@UnsplashRetrofit retrofit: Retrofit): UnsplashApi {
        return retrofit.create(UnsplashApi::class.java)
    }

    @Provides
    fun providePexelsApi(@PexelsRetrofit retrofit: Retrofit): PexelsApi {
        return retrofit.create(PexelsApi::class.java)
    }
}