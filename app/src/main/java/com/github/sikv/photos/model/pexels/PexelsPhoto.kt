package com.github.sikv.photos.model.pexels

import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PexelsPhoto(
        @SerializedName("url")
        val url: String,

        @SerializedName("width")
        val width: Int,

        @SerializedName("height")
        val height: Int,

        @SerializedName("photographer")
        val photographer: String,

        @SerializedName("photographer_url")
        val photographerUrl: String,

        @SerializedName("src")
        val src: PexelsSrc
) : Photo() {

    override fun getPhotoId(): String {
        return url.substring(url.substring(0, url.length - 1).lastIndexOf("-") + 1, url.lastIndexOf("/"))
    }

    override fun getPhotoPreviewUrl(): String {
        return src.large
    }

    override fun getPhotoFullPreviewUrl(): String {
        return src.large
    }

    override fun getPhotoDownloadUrl(): String {
        return src.large2x
    }

    override fun getPhotoShareUrl(): String {
        return url
    }

    override fun getPhotoPhotographerName(): String {
        return photographer
    }

    override fun getPhotoPhotographerImageUrl(): String? {
        return null
    }

    override fun getPhotoPhotographerUrl(): String? {
        return photographerUrl
    }

    override fun getPhotoSource(): PhotoSource {
        return PhotoSource.PEXELS
    }
}