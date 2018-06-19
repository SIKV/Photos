package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName

data class SearchPhotosResponse(
        val total: Int,

        @SerializedName("total_pages")
        val totalPages: Int,

        val results: List<Photo>
)