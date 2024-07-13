package com.github.sikv.photos.domain

import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoData(
    val id: String,
    val previewUrl: String,
    val fullPreviewUrl: String,
    val downloadUrl: String,
    val shareUrl: String,
    val photographerName: String,
    val photographerImageUrl: String?,
    val photographerUrl: String?,
    val source: PhotoSource
) : Photo() {

    override fun getPhotoId(): String = id

    override fun getPhotoPreviewUrl(): String = previewUrl
    override fun getPhotoFullPreviewUrl(): String = fullPreviewUrl
    override fun getPhotoDownloadUrl(): String = downloadUrl
    override fun getPhotoShareUrl(): String = shareUrl

    override fun getPhotoPhotographerName(): String = photographerName
    override fun getPhotoPhotographerImageUrl(): String? = photographerImageUrl
    override fun getPhotoPhotographerUrl(): String? = photographerUrl

    override fun getPhotoSource(): PhotoSource = source
}
