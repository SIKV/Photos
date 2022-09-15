package com.github.sikv.photos.domain.unsplash

import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class UnsplashPhoto(
    @SerializedName("id")
    val id: String,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("user")
    val user: UnsplashUser,

    @SerializedName("urls")
    val urls: UnsplashUrls,

    @SerializedName("links")
    val links: UnsplashLinks
) : Photo() {

    override fun getPhotoId(): String = id

    override fun getPhotoPreviewUrl(): String = urls.regular
    override fun getPhotoFullPreviewUrl(): String = urls.regular
    override fun getPhotoDownloadUrl(): String = links.download
    override fun getPhotoShareUrl(): String = links.html

    override fun getPhotoPhotographerName(): String = user.name
    override fun getPhotoPhotographerImageUrl(): String? = user.profileImage?.medium
    override fun getPhotoPhotographerUrl(): String? = user.portfolioUrl

    override fun getPhotoSource(): PhotoSource = PhotoSource.UNSPLASH
    override fun isLocalPhoto(): Boolean = false
}
