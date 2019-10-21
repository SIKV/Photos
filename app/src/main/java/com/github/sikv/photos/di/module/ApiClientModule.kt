package com.github.sikv.photos.di.module

import com.github.sikv.photos.api.PexelsApi
import com.github.sikv.photos.api.PexelsClient
import com.github.sikv.photos.api.UnsplashApi
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.util.PEXELS
import com.github.sikv.photos.util.UNSPLASH
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiClientModule {

    @Provides
    @Singleton
    fun provideUnsplashApi(@Named(UNSPLASH) retrofit: Retrofit): UnsplashClient {
        return UnsplashClient(retrofit.create(UnsplashApi::class.java))
    }

    @Provides
    @Singleton
    fun providePexelsApi(@Named(PEXELS) retrofit: Retrofit): PexelsClient {
        return PexelsClient(retrofit.create(PexelsApi::class.java))
    }
}