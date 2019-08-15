package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName

data class UnsplashSearchResponse(
        val total: Int,

        @SerializedName("total_pages")
        val totalPages: Int,

        val results: List<UnsplashPhoto>
)