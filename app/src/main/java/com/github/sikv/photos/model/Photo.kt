package com.github.sikv.photos.model

import android.os.Parcelable

abstract class Photo : Parcelable {

    /**
     * This SHOULD NOT be used directly. Use FavoritesManager#isFavorite(Photo) instead.
     */
    var favorite: Boolean = false

    open fun getPhotoId(): String = ""
    open fun getLargeUrl(): String = ""
    open fun getNormalUrl(): String = ""
    open fun getSmallUrl(): String = ""
    open fun getShareUrl(): String = ""
    open fun getPhotographerName(): String = ""
    open fun getPhotographerImageUrl(): String? = null
    open fun getPhotographerUrl(): String? = null
    open fun getSource(): String = ""
    open fun getSourceUrl(): String = ""
    open fun isLocalPhoto(): Boolean = false

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