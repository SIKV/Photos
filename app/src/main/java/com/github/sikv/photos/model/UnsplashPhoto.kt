package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

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
        val description: String,

        @SerializedName("user")
        val user: UnsplashUser,

        @SerializedName("urls")
        val urls: UnsplashUrls,

        @SerializedName("links")
        val links: UnsplashLinks
) : Photo() {

    companion object {
        const val SOURCE = "Unsplash"
    }

    override fun getPhotoId(): String {
        return id
    }

    override fun getPhotoWidth(): Int {
        return width
    }

    override fun getPhotoHeight(): Int {
        return height
    }

    override fun getPhotoCreatedAt(): Long? {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(createdAt)?.time
        } catch (e: Exception) {
            null
        }
    }

    override fun getPhotoDescription(): String? {
        return description
    }

    override fun getLargeUrl(): String {
        return urls.full
    }

    override fun getNormalUrl(): String {
        return urls.regular
    }

    override fun getSmallUrl(): String {
        return urls.small
    }

    override fun getThumbnailUrl(): String {
        return urls.regular
    }

    override fun getShareUrl(): String {
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

    override fun getPhotoSource(): String {
        return SOURCE
    }

    override fun getSourceUrl(): String {
        return links.html
    }
}