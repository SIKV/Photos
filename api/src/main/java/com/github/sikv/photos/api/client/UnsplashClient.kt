package com.github.sikv.photos.api.client

import com.github.sikv.photos.api.UnsplashApi
import com.github.sikv.photos.domain.unsplash.UnsplashPhoto
import com.github.sikv.photos.domain.unsplash.UnsplashSearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashClient @Inject constructor(
    private val unsplashApi: UnsplashApi
) {

    suspend fun getPhoto(id: String): UnsplashPhoto =
        unsplashApi.getPhoto(id)

    suspend fun searchPhotos(query: String, page: Int, perPage: Int): UnsplashSearchResponse =
        unsplashApi.searchPhotos(query, page, perPage)
}
