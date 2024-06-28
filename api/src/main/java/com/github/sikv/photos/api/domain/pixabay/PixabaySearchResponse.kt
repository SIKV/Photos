package com.github.sikv.photos.api.domain.pixabay

import com.google.gson.annotations.SerializedName

data class PixabaySearchResponse(
    @SerializedName("total")
    val total: Int,

    @SerializedName("hits")
    val hits: List<PixabayPhoto>
)
