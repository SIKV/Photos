package com.github.sikv.photos.common

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class DownloadService @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun downloadPhoto(
        photoUrl: String,
        notificationTitle: String,
        notificationDescription: String,
        saveTo: String = Environment.DIRECTORY_PICTURES
    ) {
        val request = DownloadManager.Request(Uri.parse(photoUrl))
        val filename = System.currentTimeMillis().toString() + ".jpeg"

        request
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle(notificationTitle)
            .setDescription(notificationDescription)
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(saveTo, File.separator + filename)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        downloadManager?.enqueue(request)
    }
}
