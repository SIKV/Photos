package com.github.sikv.photos.api.domain.pexels

import com.google.gson.annotations.SerializedName

data class PexelsSearchResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("par_page")
    val perPage: Int,

    @SerializedName("total_results")
    val totalResults: Int,

    @SerializedName("photos")
    val photos: List<PexelsPhoto>
)
