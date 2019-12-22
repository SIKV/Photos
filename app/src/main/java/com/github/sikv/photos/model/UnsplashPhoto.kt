package com.github.sikv.photos.model

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashPhoto(
        @SerializedName("id")
        val id: String,

        @SerializedName("description")
        val description: String?,

        @SerializedName("user")
        val user: UnsplashUser,

        @SerializedName("urls")
        val urls: UnsplashUrls,

        @SerializedName("links")
        val links: UnsplashLinks,

        private var favorite: Boolean = false
) : Photo {

    companion object {
        const val SOURCE = "Unsplash"
    }

    override fun getPhotoId(): String {
        return id
    }

    override fun getLargeUrl(): String {
        return urls.full
    }

    override fun getNormalUrl(): String {
        return urls.regular
    }

    override fun getSmallUrl(): String {
        return urls.regular
    }

    override fun getShareUrl(): String {
        return links.html
    }

    override fun getPhotographerName(): String {
        return user.name
    }

    override fun getPhotographerImageUrl(): String? {
        return user.profileImage?.medium
    }

    override fun getPhotographerUrl(): String? {
        return user.portfolioUrl
    }

    override fun getSource(): String {
        return SOURCE
    }

    override fun getSourceUrl(): String {
        return links.html
    }

    override fun isFavoritePhoto(): Boolean {
        return favorite
    }

    override fun setIsFavorite(favorite: Boolean) {
        this.favorite = favorite
    }
}