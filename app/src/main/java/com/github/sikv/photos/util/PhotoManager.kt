package com.github.sikv.photos.util

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.service.DownloadPhotoService
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoManager @Inject constructor() {

    companion object {
        private const val PREFERENCES_NAME = "photoManagerPreferences"

        private const val KEY_PHOTO_URI = "key_photo_uri"
    }

    fun downloadPhoto(context: Context, photoUrl: String) {
        DownloadPhotoService.startServiceActionDownload(context, photoUrl)
    }

    fun cancelDownloading(context: Context) {
        DownloadPhotoService.startServiceActionCancel(context)
    }

    fun startSetWallpaper(context: Context, photoUri: Uri) {
        val wallpaperManager = WallpaperManager.getInstance(context)

        try {
            val intent = wallpaperManager.getCropAndSetWallpaperIntent(photoUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)

            updateSetWallpaperState(SetWallpaperState.SUCCESS)

        } catch (e: IllegalArgumentException) {
            updateSetWallpaperState(SetWallpaperState.FAILURE)
        }
    }

    fun savePhotoUri(context: Context, uri: Uri) {
        val editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()

        editor.putString(KEY_PHOTO_URI, uri.toString())
        editor.apply()
    }

    fun getSavedPhotoUri(context: Context): Uri? {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)?.getString(KEY_PHOTO_URI, null)?.let { uriStr ->
            return Uri.parse(uriStr)
        } ?: run {
            return null
        }
    }

    fun savePhoto(context: Context, bitmap: Bitmap): Uri? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()

        val filename = "photo.png"
        val file = File(context.filesDir, filename)

        file.writeBytes(byteArray)

        return FileProvider.getUriForFile(context, context.getString(R.string._file_provider), file)
    }

    private fun updateSetWallpaperState(state: SetWallpaperState) {
        App.instance.postSetWallpaperStateLiveData(state)
    }
}