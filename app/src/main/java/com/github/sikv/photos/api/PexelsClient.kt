package com.github.sikv.photos.api

import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.pexels.PexelsCuratedPhotosResponse
import com.github.sikv.photos.model.pexels.PexelsPhoto
import com.github.sikv.photos.model.pexels.PexelsSearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelsClient @Inject constructor(
        private val pexelsApi: PexelsApi,
        private val favoritesRepository: FavoritesRepository
) {

    suspend fun getCuratedPhotos(page: Int, perPage: Int): PexelsCuratedPhotosResponse =
            pexelsApi.getCuratedPhotos(page, perPage).apply {
                photos.forEach {
                    favoritesRepository.populateFavorite(it)
                }
            }

    suspend fun getPhoto(id: String): PexelsPhoto =
            pexelsApi.getPhoto(id).apply {
                favoritesRepository.populateFavorite(this)
            }

    suspend fun searchPhotos(query: String, page: Int, perPage: Int): PexelsSearchResponse =
            pexelsApi.searchPhotos(query, page, perPage).apply {
                photos.forEach {
                    favoritesRepository.populateFavorite(it)
                }
            }
}