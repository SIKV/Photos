package com.github.sikv.photos.api

import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.pixabay.PixabaySearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixabayClient @Inject constructor(
    private val pixabayApi: PixabayApi,
    private val favoritesRepository: FavoritesRepository
) {

    suspend fun searchPhotos(query: String, page: Int, perPage: Int): PixabaySearchResponse =
        pixabayApi.searchPhotos(query, page, perPage).apply {
            hits.forEach {
                favoritesRepository.populateFavorite(it)
            }
        }

    suspend fun getPhoto(id: String): PixabaySearchResponse =
        pixabayApi.getPhoto(id).apply {
            hits.forEach {
                favoritesRepository.populateFavorite(it)
            }
        }
}
