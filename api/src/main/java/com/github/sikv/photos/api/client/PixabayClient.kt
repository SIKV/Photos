package com.github.sikv.photos.api.client

import com.github.sikv.photos.api.PixabayApi
import com.github.sikv.photos.domain.pixabay.PixabaySearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixabayClient @Inject constructor(
    private val pixabayApi: PixabayApi,
) {

    suspend fun searchPhotos(query: String, page: Int, perPage: Int): PixabaySearchResponse =
        pixabayApi.searchPhotos(query, page, perPage)

    suspend fun getPhoto(id: String): PixabaySearchResponse =
        pixabayApi.getPhoto(id)
}
