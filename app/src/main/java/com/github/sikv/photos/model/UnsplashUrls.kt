package com.github.sikv.photos.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashUrls(
        @SerializedName("raw")
        val raw: String,

        @SerializedName("full")
        val full: String,

        @SerializedName("regular")
        val regular: String,

        @SerializedName("small")
        val small: String,

        @SerializedName("thumb")
        val thumb: String
) : Parcelable