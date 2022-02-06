package com.github.sikv.photos.service

import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.sikv.photos.R
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.model.Photo
import java.io.ByteArrayOutputStream
import java.io.File

class WallpaperService(
    private val context: Activity,
    private val photoLoader: PhotoLoader,
    private val onDownloading: () -> Unit,
    private val onReady: () -> Unit,
    private val onError: () -> Unit
) {
    fun setWallpaper(photo: Photo) {
        onDownloading()

        photoLoader.load(photo.getPhotoDownloadUrl(),
            onFailed = onError,
            onPhotoReady = { bitmap ->
                saveBitmap(bitmap)?.let { uri ->
                    setWallpaper(uri) {
                        onError()
                    }
                    onReady()
                } ?: run {
                    onError()
                }
            }
        )
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
