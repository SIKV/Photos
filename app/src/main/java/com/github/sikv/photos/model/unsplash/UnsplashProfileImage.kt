package com.github.sikv.photos.model.unsplash

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashProfileImage(
        @SerializedName("small")
        val small: String,

        @SerializedName("medium")
        val medium: String,

        @SerializedName("large")
        val large: String
) : Parcelable