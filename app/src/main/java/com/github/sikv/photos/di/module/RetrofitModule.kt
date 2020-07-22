package com.github.sikv.photos.di.module

import com.github.sikv.photos.App
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

        private val unsplashKey = App.instance.getUnsplashKey()
        private val pexelsKey = App.instance.getPexelsKey()
    }

    private fun buildOkHttpClient(key: String): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", key)
                            .build()

                    chain.proceed(request)
                }
                .build()
    }

    @Provides
    @UnsplashRetrofit
    fun provideUnsplashOkHttpClient(): OkHttpClient {
       return buildOkHttpClient("Client-ID $unsplashKey")
    }

    @Provides
    @PexelsRetrofit
    fun providePexelsOkHttpClient(): OkHttpClient {
        return buildOkHttpClient(pexelsKey ?: "")
    }

    @Provides
    @UnsplashRetrofit
    fun provideUnsplashRetrofit(@UnsplashRetrofit client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(UNSPLASH_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @PexelsRetrofit
    fun providePexelsRetrofit(@PexelsRetrofit client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(PEXELS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}