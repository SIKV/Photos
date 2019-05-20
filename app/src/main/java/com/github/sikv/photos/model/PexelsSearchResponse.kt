package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName


data class PexelsSearchResponse(
        val page: Int,
        val per_page: Int,

        @SerializedName("total_results")
        val totalResults: Int,

        val photos: List<PexelsPhoto>
)