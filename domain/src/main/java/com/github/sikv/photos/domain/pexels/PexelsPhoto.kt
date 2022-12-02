package com.github.sikv.photos.domain.pexels

import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class PexelsPhoto(
    @SerializedName("id")
    val id: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("photographer")
    val photographer: String,

    @SerializedName("photographer_url")
    val photographerUrl: String,

    @SerializedName("src")
    val src: PexelsSrc
) : Photo() {

    override fun getPhotoId(): String = id

    override fun getPhotoPreviewUrl(): String = src.large
    override fun getPhotoFullPreviewUrl(): String = src.large
    override fun getPhotoDownloadUrl(): String = src.large2x
    override fun getPhotoShareUrl(): String = url

    override fun getPhotoPhotographerName(): String = photographer
    override fun getPhotoPhotographerUrl(): String = photographerUrl

    override fun getPhotoSource(): PhotoSource = PhotoSource.PEXELS
    override fun isLocalPhoto(): Boolean = false
}
