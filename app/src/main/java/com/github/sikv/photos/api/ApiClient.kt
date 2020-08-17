package com.github.sikv.photos.api

import com.github.sikv.photos.App
import javax.inject.Inject

class ApiClient private constructor() {

    companion object {
        val INSTANCE: ApiClient by lazy { ApiClient() }
    }

    @Inject
    lateinit var pexelsClient: PexelsClient

    @Inject
    lateinit var unsplashClient: UnsplashClient

    @Inject
    lateinit var pixabayClient: PixabayClient

    init {
        App.instance.networkComponent.inject(this)
    }
}