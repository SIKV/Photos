package com.github.sikv.photos.data.repository.impl

import com.github.sikv.photos.api.client.ApiClient
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
) : PhotosRepository {

    override suspend fun getPhoto(id: String, source: PhotoSource): Photo? {
        return when (source) {
            PhotoSource.PEXELS -> apiClient.pexelsClient.getPhoto(id)
            PhotoSource.UNSPLASH -> apiClient.unsplashClient.getPhoto(id)
            PhotoSource.PIXABAY -> apiClient.pixabayClient.getPhoto(id).hits.firstOrNull()
            PhotoSource.UNSPECIFIED -> throw NotImplementedError()
        }
    }

    /**
     * Uses Network First (stale-if-error) caching strategy (only for page 0).
     */
    override suspend fun getCuratedPhotos(page: Int, perPage: Int): Result<List<Photo>> {
        try {
            val photos = apiClient.pexelsClient
                .getCuratedPhotos(page + getPageNumberComplement(PhotoSource.PEXELS), perPage)
                .photos

            if (page == 0) {
                // TODO: Store in cache.
            }
            return Result.Success(photos)
        } catch (e: Exception) {
            if (page == 0) {
                // TODO: Read cache and return result.
                return Result.Error(e)
            } else {
                return Result.Error(e)
            }
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
