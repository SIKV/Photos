package com.github.sikv.photos.model

import android.content.Intent
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil

fun Photo.createShareIntent(): Intent {
    val intent = Intent()

    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, getPhotoShareUrl())
    intent.type = "text/plain"

    return intent
}

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
    open fun getPhotoCreatedAt(): Long? = null
    open fun getPhotoDescription(): String? = null

    open fun getPhotoPreviewUrl(): String = ""
    open fun getPhotoFullPreviewUrl(): String = ""
    open fun getPhotoDownloadUrl(): String = ""
    open fun getPhotoShareUrl(): String = ""

    open fun getPhotoPhotographerName(): String = ""
    open fun getPhotoPhotographerImageUrl(): String? = null
    open fun getPhotoPhotographerUrl(): String? = null
    open fun getPhotoSource(): String = ""
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