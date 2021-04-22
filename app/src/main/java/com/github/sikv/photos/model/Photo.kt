package com.github.sikv.photos.model

import android.content.Intent
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.github.sikv.photos.enumeration.PhotoSource

fun Photo.createShareIntent(): Intent {
    val intent = Intent()

    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, getPhotoShareUrl())
    intent.type = "text/plain"

    return intent
}

class PhotoDiffUtil<T : Photo> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            oldItem.getPhotoId() == newItem.getPhotoId()

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            oldItem == newItem
}

abstract class Photo : Parcelable {

    companion object {
        const val KEY = "photo"
    }

    /**
     * This SHOULD NOT be used directly. Use FavoritesManager#isFavorite(Photo) instead.
     */
    var favorite: Boolean = false

    open fun getPhotoId(): String = ""

    open fun getPhotoPreviewUrl(): String = ""
    open fun getPhotoFullPreviewUrl(): String = ""
    open fun getPhotoDownloadUrl(): String = ""
    open fun getPhotoShareUrl(): String = ""

    open fun getPhotoPhotographerName(): String = ""
    open fun getPhotoPhotographerImageUrl(): String? = null
    open fun getPhotoPhotographerUrl(): String? = null

    open fun getPhotoSource(): PhotoSource = PhotoSource.UNSPECIFIED

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