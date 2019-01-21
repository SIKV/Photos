package com.github.sikv.photos.api

import com.github.sikv.photos.model.PexelsSearchResponse
import retrofit2.Call

class PexelsClient(private val pexelsApi: PexelsApi) {

    fun searchPhotos(query: String, page: Int, perPage: Int): Call<PexelsSearchResponse> =
            pexelsApi.searchPhotos(query, page, perPage)
}