package com.github.sikv.photos.di.module

import com.github.sikv.photos.api.PexelsApi
import com.github.sikv.photos.api.UnsplashApi
import com.github.sikv.photos.util.PEXELS
import com.github.sikv.photos.util.UNSPLASH
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    fun provideUnsplashApi(@Named(UNSPLASH) retrofit: Retrofit): UnsplashApi {
        return retrofit.create(UnsplashApi::class.java)
    }

    @Provides
    fun providePexelsApi(@Named(PEXELS) retrofit: Retrofit): PexelsApi {
        return retrofit.create(PexelsApi::class.java)
    }
}