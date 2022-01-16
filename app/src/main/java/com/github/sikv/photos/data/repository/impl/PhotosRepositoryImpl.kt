package com.github.sikv.photos.data.repository.impl

import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.model.PhotoSource
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val configProvider: ConfigProvider
) : PhotosRepository {

    override suspend fun getPhoto(id: String, source: PhotoSource): Photo? {
        return when (source) {
            PhotoSource.PEXELS -> apiClient.pexelsClient.getPhoto(id)
            PhotoSource.UNSPLASH -> apiClient.unsplashClient.getPhoto(id)
            PhotoSource.PIXABAY -> apiClient.pixabayClient.getPhoto(id).hits.firstOrNull()

            else -> throw NotImplementedError()
        }
    }

    override suspend fun getCuratedPhotos(page: Int, perPage: Int): List<Photo> {
        return apiClient.pexelsClient.getCuratedPhotos(page, perPage).photos
    }

    override suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
        source: PhotoSource
    ): List<Photo> {
        return when (source) {
            PhotoSource.PEXELS -> apiClient.pexelsClient.searchPhotos(query, page, perPage).photos
            PhotoSource.UNSPLASH -> apiClient.unsplashClient.searchPhotos(
                query,
                page,
                perPage
            ).results
            PhotoSource.PIXABAY -> apiClient.pixabayClient.searchPhotos(
                query,
                page + 1,
                perPage
            ).hits

            else -> throw NotImplementedError()
        }
    }

    // TODO refactor
    override suspend fun searchPhotos(query: String, limit: Int): List<Photo> {
        return withContext(Dispatchers.IO) {
            val limitForEachSource = limit / 3

            val photos = mutableListOf<Photo>()

            try {
                val pexelsPhotos = apiClient.pexelsClient.searchPhotos(query, 0, limitForEachSource)
                photos.addAll(pexelsPhotos.photos)

            } catch (e: Exception) {
            }

            try {
                val unsplashPhotos =
                    apiClient.unsplashClient.searchPhotos(query, 0, limitForEachSource)
                photos.addAll(unsplashPhotos.results)

            } catch (e: Exception) {
            }

            try {
                val pixabayPhotos =
                    apiClient.pixabayClient.searchPhotos(query, 0, limitForEachSource)
                photos.addAll(pixabayPhotos.hits)

            } catch (e: Exception) {
            }

            photos.shuffled()
        }
    }
}
