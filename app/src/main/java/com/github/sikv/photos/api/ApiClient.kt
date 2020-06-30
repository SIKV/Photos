package com.github.sikv.photos.api

import com.github.sikv.photos.App
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiClient private constructor() {

    companion object {
        val INSTANCE: ApiClient by lazy { ApiClient() }
    }

    @Inject
    lateinit var unsplashClient: UnsplashClient

    @Inject
    lateinit var pexelsClient: PexelsClient

    init {
        App.instance.networkComponent.inject(this)
    }

    suspend fun searchPhotos(query: String, limit: Int): List<Photo> {
        return withContext(Dispatchers.IO) {
            val limitForEachSource = limit / 2

            val photos = mutableListOf<Photo>()

            try {
                val unsplashPhotos = unsplashClient.searchPhotos(query, 0, limitForEachSource)
                        .blockingGet()

                photos.addAll(unsplashPhotos.results)

            } catch (e: Exception) { }

            try {
                val pexelsPhotos = pexelsClient.searchPhotos(query, 0, limitForEachSource)
                        .blockingGet()

                photos.addAll(pexelsPhotos.photos)

            } catch (e: Exception) { }

           photos.shuffled()
        }
    }
}