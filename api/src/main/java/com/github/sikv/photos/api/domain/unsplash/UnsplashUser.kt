package com.github.sikv.photos.api.domain.unsplash

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashUser(
    @SerializedName("username")
    val username: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("portfolio_url")
    val portfolioUrl: String?,

    @SerializedName("profile_image")
    val profileImage: UnsplashProfileImage?
) : Parcelable
