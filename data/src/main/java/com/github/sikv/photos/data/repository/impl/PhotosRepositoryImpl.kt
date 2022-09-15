package com.github.sikv.photos.data.repository.impl

import com.github.sikv.photos.api.client.ApiClient
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val favoritesRepository: FavoritesRepository
) : PhotosRepository {

    override suspend fun getPhoto(id: String, source: PhotoSource): Photo? {
        return when (source) {
            PhotoSource.PEXELS -> apiClient.pexelsClient.getPhoto(id)
            PhotoSource.UNSPLASH -> apiClient.unsplashClient.getPhoto(id)
            PhotoSource.PIXABAY -> apiClient.pixabayClient.getPhoto(id).hits.firstOrNull()
            PhotoSource.UNSPECIFIED -> throw NotImplementedError()
        }?.apply {
            favoritesRepository.populateFavorite(this)
        }
    }

    override suspend fun getCuratedPhotos(page: Int, perPage: Int): List<Photo> {
        return apiClient.pexelsClient
            .getCuratedPhotos(page + getPageNumberComplement(PhotoSource.PEXELS), perPage)
            .photos
            .onEach { photo ->
                favoritesRepository.populateFavorite(photo)
            }
    }

    override suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
        source: PhotoSource
    ): List<Photo> {
        val pageWithComplement = page + getPageNumberComplement(source)

        return when (source) {
            PhotoSource.PEXELS -> apiClient.pexelsClient
                .searchPhotos(query, pageWithComplement, perPage)
                .photos

            PhotoSource.UNSPLASH -> apiClient.unsplashClient
                .searchPhotos(query, pageWithComplement, perPage)
                .results

            PhotoSource.PIXABAY -> apiClient.pixabayClient
                .searchPhotos(query, pageWithComplement, perPage)
                .hits

            PhotoSource.UNSPECIFIED -> throw NotImplementedError()
        }.onEach { photo ->
            favoritesRepository.populateFavorite(photo)
        }
    }

    private fun getPageNumberComplement(source: PhotoSource): Int {
        return when (source) {
            PhotoSource.PEXELS -> 1
            PhotoSource.UNSPLASH -> 0
            PhotoSource.PIXABAY -> 1
            PhotoSource.UNSPECIFIED -> 0
        }
    }
}
