package com.github.sikv.photos.api

import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.unsplash.UnsplashPhoto
import com.github.sikv.photos.model.unsplash.UnsplashSearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashClient @Inject constructor(
        private val unsplashApi: UnsplashApi,
        private val favoritesRepository: FavoritesRepository
) {

    suspend fun getPhoto(id: String): UnsplashPhoto =
            unsplashApi.getPhoto(id).apply {
                favoritesRepository.populateFavorite(this)
            }

    suspend fun searchPhotos(query: String, page: Int, perPage: Int): UnsplashSearchResponse =
            unsplashApi.searchPhotos(query, page, perPage).apply {
                results.forEach {
                    favoritesRepository.populateFavorite(it)
                }
            }
}