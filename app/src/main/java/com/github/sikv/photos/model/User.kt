package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName

data class User(
        val username: String,
        val name: String,

        @SerializedName("portfolio_url")
        val portfolioUrl: String,

        @SerializedName("profile_image")
        val profileImage: ProfileImage
)