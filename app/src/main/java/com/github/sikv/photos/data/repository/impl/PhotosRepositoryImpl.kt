package com.github.sikv.photos.data.repository.impl

import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.enumeration.SearchSource
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosRepositoryImpl @Inject constructor() : PhotosRepository {

    private val api = ApiClient.INSTANCE

    override suspend fun getPhoto(id: String, source: PhotoSource): Photo? {
        return when (source) {
            PhotoSource.PEXELS -> api.pexelsClient.getPhoto(id)
            PhotoSource.UNSPLASH -> api.unsplashClient.getPhoto(id)
            PhotoSource.PIXABAY -> api.pixabayClient.getPhoto(id).hits.firstOrNull()

            else -> throw NotImplementedError()
        }
    }

    override suspend fun getLatestPhotos(page: Int, perPage: Int, source: PhotoSource): List<Photo> {
        return when (source) {
            PhotoSource.PEXELS -> api.pexelsClient.getCuratedPhotos(page, perPage).photos

            else -> throw NotImplementedError()
        }
    }

    override suspend fun searchPhotos(query: String, page: Int, perPage: Int, source: PhotoSource): List<Photo> {
        return when (source) {
            PhotoSource.PEXELS -> api.pexelsClient.searchPhotos(query, page, perPage).photos
            PhotoSource.UNSPLASH -> api.unsplashClient.searchPhotos(query, page, perPage).results
            PhotoSource.PIXABAY -> api.pixabayClient.searchPhotos(query, page + 1, perPage).hits

            else -> throw NotImplementedError()
        }
    }

    override suspend fun searchPhotos(query: String, limit: Int): List<Photo> {
        return withContext(Dispatchers.IO) {
            val limitForEachSource = limit / SearchSource.size

            val photos = mutableListOf<Photo>()

            try {
                val pexelsPhotos = ApiClient.INSTANCE.pexelsClient.searchPhotos(query, 0, limitForEachSource)
                photos.addAll(pexelsPhotos.photos)

            } catch (e: Exception) { }

            try {
                val unsplashPhotos = ApiClient.INSTANCE.unsplashClient.searchPhotos(query, 0, limitForEachSource)
                photos.addAll(unsplashPhotos.results)

            } catch (e: Exception) { }

            try {
                val pixabayPhotos = ApiClient.INSTANCE.pixabayClient.searchPhotos(query, 0, limitForEachSource)
                photos.addAll(pixabayPhotos.hits)

            } catch (e: Exception) { }

            photos.shuffled()
        }
    }
}