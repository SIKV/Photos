package com.github.sikv.photos.api

import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.pexels.PexelsCuratedPhotosResponse
import com.github.sikv.photos.model.pexels.PexelsSearchResponse
import com.github.sikv.photos.model.Photo
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelsClient @Inject constructor(
        private val pexelsApi: PexelsApi,
        private val favoritesRepository: FavoritesRepository) {

    fun getCuratedPhotos(page: Int, perPage: Int): Single<PexelsCuratedPhotosResponse> =
            pexelsApi.getCuratedPhotos(page, perPage)
                    .map(favoritesRepository::populateFavorite)

    fun getPhoto(id: String): Single<Photo> =
            pexelsApi.getPhoto(id)
                    .map(favoritesRepository::populateFavorite)

    fun searchPhotos(query: String, page: Int, perPage: Int): Single<PexelsSearchResponse> =
            pexelsApi.searchPhotos(query, page, perPage)
                    .map(favoritesRepository::populateFavorite)
}