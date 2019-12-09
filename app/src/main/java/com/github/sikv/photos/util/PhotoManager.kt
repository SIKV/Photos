package com.github.sikv.photos.util

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.sikv.photos.R
import com.github.sikv.photos.service.SetWallpaperService
import java.io.ByteArrayOutputStream
import java.io.File

// TODO Use DI
object PhotoManager {

    fun setWallpaper(context: Context, photoUrl: String) {
        SetWallpaperService.startService(context, photoUrl)
    }

    fun setWallpaper(context: Context, photoUri: Uri) {
        val wallpaperManager = WallpaperManager.getInstance(context)

        val intent = wallpaperManager.getCropAndSetWallpaperIntent(photoUri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }

    fun savePhoto(context: Context, bitmap: Bitmap): Uri? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()

        val filename = "photo.png"
        val file = File(context.filesDir, filename)

        file.writeBytes(byteArray)

        return FileProvider.getUriForFile(context, context.getString(R.string._file_provider), file)
    }
}