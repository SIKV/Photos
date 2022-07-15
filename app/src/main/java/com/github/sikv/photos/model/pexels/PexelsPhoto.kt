package com.github.sikv.photos.model.pexels

import com.github.sikv.photos.model.PhotoSource
import com.github.sikv.photos.model.Photo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
        return url.substring(
            url.substring(0, url.length - 1).lastIndexOf("-") + 1,
            url.lastIndexOf("/")
        )
    }

    override fun getPhotoPreviewUrl(): String = src.large
    override fun getPhotoFullPreviewUrl(): String = src.large
    override fun getPhotoDownloadUrl(): String = src.large2x
    override fun getPhotoShareUrl(): String = url

    override fun getPhotoPhotographerName(): String = photographer
    override fun getPhotoPhotographerUrl(): String = photographerUrl

    override fun getPhotoSource(): PhotoSource = PhotoSource.PEXELS
    override fun isLocalPhoto(): Boolean = false
}
