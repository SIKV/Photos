package com.github.sikv.photos.util

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.SetWallpaperState
import com.github.sikv.photos.service.DownloadPhotoService
import java.io.ByteArrayOutputStream
import java.io.File

private const val KEY_PHOTO_URI = "key_photo_uri"

fun Context.downloadPhoto(photoUrl: String) {
    DownloadPhotoService.startServiceActionDownload(this, photoUrl)
}

fun Context.cancelPhotoDownloading() {
    DownloadPhotoService.startServiceActionCancel(this)
}

fun Context.startSetWallpaperActivity(photoUri: Uri) {
    val wallpaperManager = WallpaperManager.getInstance(this)

    try {
        val intent = wallpaperManager.getCropAndSetWallpaperIntent(photoUri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)

        App.instance.postSetWallpaperState(SetWallpaperState.SUCCESS)

    } catch (e: IllegalArgumentException) {
        App.instance.postSetWallpaperState(SetWallpaperState.FAILURE)
    }
}

fun Context.savePhotoInFile(bitmap: Bitmap): Uri? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

    val byteArray = byteArrayOutputStream.toByteArray()

    val filename = "photo.png"
    val file = File(filesDir, filename)

    file.writeBytes(byteArray)

    return FileProvider.getUriForFile(this, getString(R.string._file_provider), file)
}

fun Context.getSavedPhotoUri(): Uri? {
    App.instance.getPrivatePreferences().getString(KEY_PHOTO_URI, null)?.let { uriStr ->
        return Uri.parse(uriStr)
    } ?: run {
        return null
    }
}

fun Context.savePhotoUri(uri: Uri) {
    val editor = App.instance.getPrivatePreferences()
            .edit()

    editor.putString(KEY_PHOTO_URI, uri.toString())
    editor.apply()
}