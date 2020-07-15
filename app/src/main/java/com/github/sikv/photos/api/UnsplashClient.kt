package com.github.sikv.photos.api

import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.unsplash.UnsplashSearchResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashClient @Inject constructor(
        private val unsplashApi: UnsplashApi,
        private val favoritesRepository: FavoritesRepository) {

    fun getPhoto(id: String): Single<Photo> =
            unsplashApi.getPhoto(id)
                    .map(favoritesRepository::populateFavorite)

    fun searchPhotos(query: String, page: Int, perPage: Int): Single<UnsplashSearchResponse> =
            unsplashApi.searchPhotos(query, page, perPage)
                    .map(favoritesRepository::populateFavorite)
}