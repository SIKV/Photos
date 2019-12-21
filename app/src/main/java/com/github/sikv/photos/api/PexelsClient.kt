package com.github.sikv.photos.api

import com.github.sikv.photos.model.PexelsCuratedPhotosResponse
import com.github.sikv.photos.model.PexelsPhoto
import com.github.sikv.photos.model.PexelsSearchResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelsClient @Inject constructor(private val pexelsApi: PexelsApi) {

    fun getCuratedPhotos(page: Int, perPage: Int): Single<PexelsCuratedPhotosResponse> =
            pexelsApi.getCuratedPhotos(page, perPage)

    fun searchPhotos(query: String, page: Int, perPage: Int): Single<PexelsSearchResponse> =
            pexelsApi.searchPhotos(query, page, perPage)

    fun getPhoto(id: String): Single<PexelsPhoto> =
            pexelsApi.getPhoto(id)
}