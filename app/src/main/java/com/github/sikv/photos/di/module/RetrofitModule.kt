package com.github.sikv.photos.di.module

import com.github.sikv.photos.Keys
import com.github.sikv.photos.enumeration.PhotoSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class PexelsRetrofit

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class UnsplashRetrofit

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class PixabayRetrofit

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    companion object {
        private const val PEXELS_BASE_URL = "https://api.pexels.com/v1/"
        private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
        private const val PIXABAY_BASE_URL = "https://pixabay.com/api/"

        private val pexelsKey = Keys.getPexelsKey()
        private val unsplashKey = Keys.getUnsplashKey()
        private val pixabayKey = Keys.getPixabayKey()
    }

    private fun buildOkHttpClient(key: String, photoSource: PhotoSource): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    when (photoSource) {
                        PhotoSource.PEXELS, PhotoSource.UNSPLASH -> {
                            val request = chain.request()
                                    .newBuilder()
                                    .addHeader("Authorization", key)
                                    .build()

                            chain.proceed(request)
                        }

                        PhotoSource.PIXABAY -> {
                            val httpUrl = chain.request()
                                    .url
                                    .newBuilder()
                                    .addQueryParameter("key", key)
                                    .build()

                            val request = chain.request()
                                    .newBuilder()
                                    .url(httpUrl)
                                    .build()

                            chain.proceed(request)
                        }

                        else -> chain.proceed(chain.request())
                    }
                }
                .build()
    }

    @Provides
    @PexelsRetrofit
    fun providePexelsOkHttpClient(): OkHttpClient {
        return buildOkHttpClient(pexelsKey ?: "", PhotoSource.PEXELS)
    }

    @Provides
    @UnsplashRetrofit
    fun provideUnsplashOkHttpClient(): OkHttpClient {
        return buildOkHttpClient("Client-ID $unsplashKey", PhotoSource.UNSPLASH)
    }

    @Provides
    @PixabayRetrofit
    fun providePixabayOkHttpClient(): OkHttpClient {
        return buildOkHttpClient(pixabayKey ?: "", PhotoSource.PIXABAY)
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
    @PixabayRetrofit
    fun providePixabayRetrofit(@PixabayRetrofit client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(PIXABAY_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}