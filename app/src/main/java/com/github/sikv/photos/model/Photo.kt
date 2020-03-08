package com.github.sikv.photos.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil

abstract class Photo : Parcelable {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                    oldItem.getPhotoId() == newItem.getPhotoId()

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                    oldItem == newItem
        }
    }

    /**
     * This SHOULD NOT be used directly. Use FavoritesManager#isFavorite(Photo) instead.
     */
    var favorite: Boolean = false

    open fun getPhotoId(): String = ""
    open fun getPhotoWidth(): Int = 0
    open fun getPhotoHeight(): Int = 0
    open fun getLargeUrl(): String = ""
    open fun getNormalUrl(): String = ""
    open fun getSmallUrl(): String = ""
    open fun getThumbnailUrl(): String = ""
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