package com.github.sikv.photos.api

import com.github.sikv.photos.App
import com.github.sikv.photos.model.PexelsSearchResponse
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashSearchResponse
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class ApiClient private constructor() {

    companion object {
        val INSTANCE: ApiClient by lazy { ApiClient() }
    }

    @Inject
    lateinit var unsplashClient: UnsplashClient

    @Inject
    lateinit var pexelsClient: PexelsClient

    init {
        App.instance.networkComponent.inject(this)
    }

    fun searchPhotos(query: String, limit: Int): Single<List<Photo>> {
        val limitForEachSource = limit / 2

        val unsplashSearch = unsplashClient.searchPhotos(query, 0, limitForEachSource)
        val pexelsSearch = pexelsClient.searchPhotos(query, 0, limitForEachSource)

        return Single.zip(unsplashSearch, pexelsSearch, BiFunction<UnsplashSearchResponse, PexelsSearchResponse, List<Photo>> { unsplashPhotos, pexelsPhotos ->
            (unsplashPhotos.results + pexelsPhotos.photos).shuffled()
        })
    }
}