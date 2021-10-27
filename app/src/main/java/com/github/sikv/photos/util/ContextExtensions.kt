package com.github.sikv.photos.util

import android.app.Activity
import android.app.DownloadManager
import android.app.WallpaperManager
import android.app.WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER
import android.content.*
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.browser.customtabs.CustomTabsIntent
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import java.io.File

fun Context.showSoftInput(view: View): Boolean {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return inputMethodManager.showSoftInput(view, 0)
}

fun Context.hideSoftInput(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideSoftInput() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Context.openUrl(url: String) {
    val builder = CustomTabsIntent.Builder()
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

    } catch (e: IllegalArgumentException) {
        try {
            val intent = Intent(ACTION_CROP_AND_SET_WALLPAPER, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            App.instance.postGlobalMessage(getString(R.string.error_setting_wallpaper))
        }
    }
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri

    startActivity(intent)
}

fun Context.copyText(label: String, text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)

    clipboardManager.setPrimaryClip(clipData)
}