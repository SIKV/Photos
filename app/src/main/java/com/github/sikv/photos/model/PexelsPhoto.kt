package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PexelsPhoto(
        @SerializedName("url")
        val url: String,

        @SerializedName("photographer")
        val photographer: String,

        @SerializedName("src")
        val src: PexelsSrc
) : Photo() {

    companion object {
        const val SOURCE = "Pexels"
    }

    override fun getPhotoId(): String {
        return url.substring(url.substring(0, url.length - 1).lastIndexOf("-") + 1, url.lastIndexOf("/"))
    }

    override fun getLargeUrl(): String {
        return src.large2x
    }

    override fun getNormalUrl(): String {
        return src.large
    }

    override fun getSmallUrl(): String {
        return src.large
    }

    override fun getShareUrl(): String {
        return url
    }

    override fun getPhotographerName(): String {
        return photographer
    }

    override fun getPhotographerImageUrl(): String? {
        return null
    }

    override fun getPhotographerUrl(): String? {
        return null
    }

    override fun getSource(): String {
        return SOURCE
    }

    override fun getSourceUrl(): String {
        return url
    }
}