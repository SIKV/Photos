package com.github.sikv.photos.domain.unsplash

import com.google.gson.annotations.SerializedName

data class UnsplashSearchResponse(
    @SerializedName("total")
    val total: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("results")
    val results: List<UnsplashPhoto>
)
