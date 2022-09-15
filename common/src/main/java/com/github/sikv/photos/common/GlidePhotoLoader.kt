package com.github.sikv.photos.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GlidePhotoLoader @Inject constructor(
    @ApplicationContext context: Context
) : PhotoLoader {

    private val glide = Glide.with(context)
    private val transition = DrawableTransitionOptions.withCrossFade(400)

    override fun load(url: String?, imageView: ImageView) {
        glide.load(url)
            .transition(transition)
            .into(imageView)
    }

    override suspend fun load(url: String?): Bitmap? {
        return suspendCoroutine { c ->
            load(url = url,
                onFailed = {
                    c.resume(null)
                },
                onCleared = {
                    c.resume(null)
                },
                onPhotoReady = { bitmap ->
                    c.resume(bitmap)
                }
            )
        }
    }

    override fun load(
        url: String?,
        onFailed: (() -> Unit)?,
        onCleared: (() -> Unit)?,
        onPhotoReady: (Bitmap) -> Unit
    ) {
        glide.asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    onPhotoReady(bitmap)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    onFailed?.invoke()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    onCleared?.invoke()
                }
            })
    }

    override fun loadWithCircleCrop(
        url: String?,
        placeholder: BitmapDrawable?,
        imageView: ImageView
    ) {
        glide.load(url)
            .transition(transition)
            .transform(CircleCrop())
            .placeholder(placeholder)
            .into(imageView)
    }
}
