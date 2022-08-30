package com.github.sikv.photos.manager

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

interface PhotoLoader {
    fun load(url: String?, imageView: ImageView)

    suspend fun load(url: String?): Bitmap?

    fun load(
        url: String?,
        onFailed: (() -> Unit)? = null,
        onCleared: (() -> Unit)? = null,
        onPhotoReady: (Bitmap) -> Unit
    )

    fun loadWithCircleCrop(url: String?, placeholder: BitmapDrawable?, imageView: ImageView)
}
