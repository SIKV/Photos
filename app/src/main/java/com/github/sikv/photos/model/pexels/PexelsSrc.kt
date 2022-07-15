package com.github.sikv.photos.model.pexels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PexelsSrc(
    @SerializedName("original")
    val original: String,

    @SerializedName("large")
    val large: String,

    @SerializedName("large2x")
    val large2x: String,

    @SerializedName("medium")
    val medium: String,

    @SerializedName("small")
    val small: String,

    @SerializedName("portrait")
    val portrait: String,

    @SerializedName("landscape")
    val landscape: String,

    @SerializedName("tiny")
    val tiny: String
) : Parcelable
