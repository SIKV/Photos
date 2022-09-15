package com.github.sikv.photos.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
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
