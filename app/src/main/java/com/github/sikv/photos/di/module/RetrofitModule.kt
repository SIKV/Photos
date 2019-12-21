package com.github.sikv.photos.di.module

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class UnsplashRetrofit

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class PexelsRetrofit

@Module
class RetrofitModule {

    companion object {
        private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
        private const val PEXELS_BASE_URL = "https://api.pexels.com/v1/"
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
    }

    @Provides
    @UnsplashRetrofit
    fun provideUnsplashRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(UNSPLASH_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @PexelsRetrofit
    fun providePexelsRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(PEXELS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
}