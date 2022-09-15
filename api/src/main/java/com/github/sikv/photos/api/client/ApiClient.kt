package com.github.sikv.photos.api.client

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    val pexelsClient: PexelsClient,
    val unsplashClient: UnsplashClient,
    val pixabayClient: PixabayClient
)
