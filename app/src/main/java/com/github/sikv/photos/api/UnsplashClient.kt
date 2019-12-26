package com.github.sikv.photos.api

import com.github.sikv.photos.data.FavoritesRepository
import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.model.UnsplashSearchResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashClient @Inject constructor(
        private val unsplashApi: UnsplashApi,
        private val favoritesRepository: FavoritesRepository) {

    fun getLatestPhotos(page: Int, perPage: Int): Single<List<UnsplashPhoto>> =
            unsplashApi.getPhotos(page, perPage, "latest")
                    .map(favoritesRepository::populateFavorite)

    fun getPhoto(id: String): Single<UnsplashPhoto> =
            unsplashApi.getPhoto(id)
                    .map(favoritesRepository::populateFavorite)

    fun searchPhotos(query: String, page: Int, perPage: Int): Single<UnsplashSearchResponse> =
            unsplashApi.searchPhotos(query, page, perPage)
                    .map(favoritesRepository::populateFavorite)
}