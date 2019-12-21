package com.github.sikv.photos.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashLinks(
        @SerializedName("self")
        val self: String,

        @SerializedName("html")
        val html: String,

        @SerializedName("download")
        val download: String
) : Parcelable