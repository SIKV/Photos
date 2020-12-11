package com.github.sikv.photos.di.module

import com.github.sikv.photos.api.PexelsApi
import com.github.sikv.photos.api.PixabayApi
import com.github.sikv.photos.api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {

    @Provides
    fun providePexelsApi(@PexelsRetrofit retrofit: Retrofit): PexelsApi {
        return retrofit.create(PexelsApi::class.java)
    }

    @Provides
    fun provideUnsplashApi(@UnsplashRetrofit retrofit: Retrofit): UnsplashApi {
        return retrofit.create(UnsplashApi::class.java)
    }

    @Provides
    fun providePixabayApi(@PixabayRetrofit retrofit: Retrofit): PixabayApi {
        return retrofit.create(PixabayApi::class.java)
    }
}