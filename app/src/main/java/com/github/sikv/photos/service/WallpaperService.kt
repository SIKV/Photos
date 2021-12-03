package com.github.sikv.photos.service

import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.FileProvider
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import java.io.ByteArrayOutputStream
import java.io.File

class WallpaperService(
    private val context: Activity,
    private val glide: RequestManager,
    private val onDownloading: () -> Unit,
    private val onReady: () -> Unit,
    private val onError: () -> Unit
) {

    fun setWallpaper(photo: Photo) {
        onDownloading()

        glide.asBitmap()
            .load(photo.getPhotoDownloadUrl())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    saveBitmap(bitmap)?.let { uri ->
                        setWallpaper(uri) {
                            onError()
                        }
                        onReady()
                    } ?: run {
                        onError()
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)

                    onError()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun setWallpaper(photoUri: Uri, onError: () -> Unit) {
        val wallpaperManager = WallpaperManager.getInstance(context)

        try {
            val intent = wallpaperManager.getCropAndSetWallpaperIntent(photoUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)

        } catch (e: IllegalArgumentException) {
            try {
                val intent = Intent(WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER, photoUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                onError()
            }
        }
    }

    private fun saveBitmap(photo: Bitmap): Uri? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()

        val filename = "photo.jpeg"
        val file = File(context.filesDir, filename)

        file.writeBytes(byteArray)
        return FileProvider.getUriForFile(context, context.getString(R.string._file_provider), file)
    }
}
