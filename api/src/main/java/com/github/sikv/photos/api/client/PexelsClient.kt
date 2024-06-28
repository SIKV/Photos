package com.github.sikv.photos.api.client

import com.github.sikv.photos.api.PexelsApi
import com.github.sikv.photos.api.domain.pexels.PexelsCuratedPhotosResponse
import com.github.sikv.photos.api.domain.pexels.PexelsPhoto
import com.github.sikv.photos.api.domain.pexels.PexelsSearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelsClient @Inject constructor(
    private val pexelsApi: PexelsApi
) {

    suspend fun getCuratedPhotos(page: Int, perPage: Int): PexelsCuratedPhotosResponse =
        pexelsApi.getCuratedPhotos(page, perPage)

    suspend fun getPhoto(id: String): PexelsPhoto =
        pexelsApi.getPhoto(id)

    suspend fun searchPhotos(query: String, page: Int, perPage: Int): PexelsSearchResponse =
        pexelsApi.searchPhotos(query, page, perPage)
}
