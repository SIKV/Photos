package com.github.sikv.photos.model.pixabay

import com.github.sikv.photos.model.PhotoSource
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
    val originalImageUrl: String,

    @SerializedName("fullHDURL")
    val fullHDUrl: String,

    @SerializedName("largeImageURL")
    val largeImageUrl: String,

    @SerializedName("user")
    val user: String,

    @SerializedName("userImageURL")
    val userImageUrl: String
) : Photo() {

    override fun getPhotoId(): String = id.toString()

    override fun getPhotoPreviewUrl(): String = largeImageUrl
    override fun getPhotoFullPreviewUrl(): String = fullHDUrl
    override fun getPhotoDownloadUrl(): String = originalImageUrl
    override fun getPhotoShareUrl(): String = pageUrl

    override fun getPhotoPhotographerName(): String = user
    override fun getPhotoPhotographerImageUrl(): String = userImageUrl

    override fun getPhotoSource(): PhotoSource = PhotoSource.PIXABAY
    override fun isLocalPhoto(): Boolean = false
}
