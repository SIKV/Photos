package com.github.sikv.photos.model

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.github.sikv.photos.R
import com.google.android.material.color.MaterialColors

fun Photo.createShareIntent(): Intent {
    val intent = Intent()

    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, getPhotoShareUrl())
    intent.type = "text/plain"

    return intent
}

fun Photo.getAttributionPlaceholderTextColor(context: Context): Int = MaterialColors
    .getColor(context, R.attr.colorOnPrimaryContainer, Color.WHITE)

fun Photo.getAttributionPlaceholderBackgroundColor(context: Context): Int = MaterialColors
    .getColor(context, R.attr.colorPrimaryContainer, Color.BLACK)

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
