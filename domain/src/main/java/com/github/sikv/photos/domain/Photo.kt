package com.github.sikv.photos.domain

import android.os.Parcelable

abstract class Photo : Parcelable {

    /**
     * This SHOULD NOT be used directly. Use FavoritesManager#isFavorite(Photo) instead.
     */
    var favorite: Boolean = false

    abstract fun getPhotoId(): String

    abstract fun getPhotoPreviewUrl(): String
    abstract fun getPhotoFullPreviewUrl(): String
    abstract fun getPhotoDownloadUrl(): String
    abstract fun getPhotoShareUrl(): String

    abstract fun getPhotoPhotographerName(): String
    open fun getPhotoPhotographerImageUrl(): String? = null
    open fun getPhotoPhotographerUrl(): String? = null

    open fun getPhotoSource(): PhotoSource = PhotoSource.UNSPECIFIED

    abstract fun isLocalPhoto(): Boolean

    override fun equals(other: Any?): Boolean {
        return if (other !is Photo) {
            false
        } else {
            this.getPhotoId() == other.getPhotoId()
        }
    }

    override fun hashCode(): Int {
        return getPhotoId().hashCode()
    }
}
