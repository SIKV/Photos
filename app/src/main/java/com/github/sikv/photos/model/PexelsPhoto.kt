package com.github.sikv.photos.model

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

        @SerializedName("src")
        val src: PexelsSrc
) : Photo() {

    companion object {
        const val SOURCE = "Pexels"
    }

    override fun getPhotoId(): String {
        return url.substring(url.substring(0, url.length - 1).lastIndexOf("-") + 1, url.lastIndexOf("/"))
    }

    override fun getPhotoWidth(): Int {
        return width
    }

    override fun getPhotoHeight(): Int {
        return height
    }

    override fun getLargeUrl(): String {
        return src.large2x
    }

    override fun getNormalUrl(): String {
        return src.medium
    }

    override fun getSmallUrl(): String {
        return src.small
    }

    override fun getThumbnailUrl(): String {
        return src.large
    }

    override fun getShareUrl(): String {
        return url
    }

    override fun getPhotoPhotographerName(): String {
        return photographer
    }

    override fun getPhotoPhotographerImageUrl(): String? {
        return null
    }

    override fun getPhotoPhotographerUrl(): String? {
        return null
    }

    override fun getPhotoSource(): String {
        return SOURCE
    }

    override fun getSourceUrl(): String {
        return url
    }
}