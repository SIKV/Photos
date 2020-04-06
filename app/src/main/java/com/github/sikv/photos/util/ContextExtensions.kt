package com.github.sikv.photos.util

import android.app.Activity
import android.app.DownloadManager
import android.app.WallpaperManager
import android.app.WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.SetWallpaperResultState
import com.github.sikv.photos.event.Event
import java.io.ByteArrayOutputStream
import java.io.File

fun Context.showSoftInput(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun Context.hideSoftInput(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideSoftInput() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Context.navigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

fun Context.openUrl(url: String) {
    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))

    val intent = builder.build()
    intent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    intent.launchUrl(this, Uri.parse(url))
}

fun Context.downloadPhotoAndSaveToPictures(photoUrl: String) {
    val request = DownloadManager.Request(Uri.parse(photoUrl))

    val filename = System.currentTimeMillis().toString() + ".jpeg"

    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.downloading_photo))
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename)

    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
    downloadManager?.enqueue(request)
}

fun Context.startSetWallpaperActivity(photoUri: Uri) {
    val wallpaperManager = WallpaperManager.getInstance(this)

    try {
        val intent = wallpaperManager.getCropAndSetWallpaperIntent(photoUri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
        App.instance.postSetWallpaperResultStateEvent(Event(SetWallpaperResultState.SUCCESS))

    } catch (e: IllegalArgumentException) {
        try {
            val intent = Intent(ACTION_CROP_AND_SET_WALLPAPER, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
            App.instance.postSetWallpaperResultStateEvent(Event(SetWallpaperResultState.SUCCESS))

        } catch (e: ActivityNotFoundException) {
            App.instance.postSetWallpaperResultStateEvent(Event(SetWallpaperResultState.FAILURE))
        }
    }
}

fun Context.savePhotoInFile(bitmap: Bitmap): Uri? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

    val byteArray = byteArrayOutputStream.toByteArray()

    val filename = "photo.jpeg"
    val file = File(filesDir, filename)

    file.writeBytes(byteArray)

    return FileProvider.getUriForFile(this, getString(R.string._file_provider), file)
}