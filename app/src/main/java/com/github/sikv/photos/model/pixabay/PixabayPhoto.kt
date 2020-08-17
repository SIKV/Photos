package com.github.sikv.photos.model.pixabay

import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PixabayPhoto(
        @SerializedName("id")
        val id: Long,

        @SerializedName("pageURL")
        val pageUrl: String,

        @SerializedName("imageURL")
        val imageUrl: String,

        @SerializedName("fullHDURL")
        val fullHDUrl: String,

        @SerializedName("largeImageURL")
        val largeImageUrl: String,

        @SerializedName("user")
        val user: String,

        @SerializedName("userImageURL")
        val userImageUrl: String
) : Photo() {

    override fun getPhotoId(): String {
        return id.toString()
    }

    override fun getPhotoPreviewUrl(): String {
        return imageUrl
    }

    override fun getPhotoFullPreviewUrl(): String {
        return fullHDUrl
    }

    override fun getPhotoDownloadUrl(): String {
        return largeImageUrl
    }

    override fun getPhotoShareUrl(): String {
        return pageUrl
    }

    override fun getPhotoPhotographerName(): String {
        return user
    }

    override fun getPhotoPhotographerImageUrl(): String? {
        return userImageUrl
    }

    override fun getPhotoPhotographerUrl(): String? {
        return null
    }

    override fun getPhotoSource(): PhotoSource {
        return PhotoSource.PIXABAY
    }
}