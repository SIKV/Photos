package com.github.sikv.photos.api.domain.unsplash

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashProfileImage(
    @SerializedName("small")
    val small: String,

    @SerializedName("medium")
    val medium: String,

    @SerializedName("large")
    val large: String
) : Parcelable
