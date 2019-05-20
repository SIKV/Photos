package com.github.sikv.photos.model


data class PexelsCuratedPhotosResponse(
        val page: Int,
        val per_page: Int,

        val photos: List<PexelsPhoto>
)