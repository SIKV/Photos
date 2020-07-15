package com.github.sikv.photos.model.unsplash

import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

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

    override fun getPhotoId(): String {
        return id
    }

    override fun getPhotoPreviewUrl(): String {
        return urls.regular
    }

    override fun getPhotoFullPreviewUrl(): String {
        return urls.regular
    }

    override fun getPhotoDownloadUrl(): String {
        return links.download
    }

    override fun getPhotoShareUrl(): String {
        return links.html
    }

    override fun getPhotoPhotographerName(): String {
        return user.name
    }

    override fun getPhotoPhotographerImageUrl(): String? {
        return user.profileImage?.medium
    }

    override fun getPhotoPhotographerUrl(): String? {
        return user.portfolioUrl
    }

    override fun getPhotoSource(): PhotoSource {
        return PhotoSource.UNSPLASH
    }
}