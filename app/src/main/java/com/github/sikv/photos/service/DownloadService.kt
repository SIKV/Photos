package com.github.sikv.photos.service

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.github.sikv.photos.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class DownloadService @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun downloadPhoto(
        photoUrl: String,
        saveTo: String = Environment.DIRECTORY_PICTURES
    ) {
        val request = DownloadManager.Request(Uri.parse(photoUrl))

        val filename = System.currentTimeMillis().toString() + ".jpeg"

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle(context.getString(R.string.app_name))
            .setDescription(context.getString(R.string.downloading_photo))
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(saveTo, File.separator + filename)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        downloadManager?.enqueue(request)
    }
}
