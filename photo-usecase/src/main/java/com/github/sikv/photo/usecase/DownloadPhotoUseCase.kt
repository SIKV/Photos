package com.github.sikv.photo.usecase

import android.Manifest
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.github.sikv.photos.common.ActivityPermissionManager
import com.github.sikv.photos.common.DownloadService
import com.github.sikv.photos.common.ui.openAppSettings
import com.github.sikv.photos.domain.Photo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class DownloadPhotoUseCase @Inject constructor(
    private val downloadService: DownloadService
) {

    fun download(activity: FragmentActivity, photo: Photo, onShowMessage: (String) -> Unit) {
        val permissionManager = ActivityPermissionManager(activity)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // No need to request WRITE_EXTERNAL_STORAGE permission on Android 11 and higher
            downloadPhotoInternal(activity, photo, onShowMessage)
        } else {
            // Request WRITE_EXTERNAL_STORAGE permission on Android 10 and lower
            permissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
                if (granted) {
                    downloadPhotoInternal(activity, photo, onShowMessage)
                } else {
                    MaterialAlertDialogBuilder(activity)
                        .setTitle(R.string.storage_permission)
                        .setMessage(R.string.storage_permission_description)
                        .setPositiveButton(R.string.open_settings) { _, _ ->
                            activity.openAppSettings()
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show()
                }
            }
        }
    }

    private fun downloadPhotoInternal(
        activity: FragmentActivity,
        photo: Photo,
        onShowMessage: (String) -> Unit
    ) {
        downloadService.downloadPhoto(
            photoUrl = photo.getPhotoDownloadUrl(),
            notificationTitle = activity.getString(R.string.photos),
            notificationDescription = activity.getString(R.string.downloading_photo)
        )
        onShowMessage(activity.getString(R.string.downloading_photo))
    }
}
