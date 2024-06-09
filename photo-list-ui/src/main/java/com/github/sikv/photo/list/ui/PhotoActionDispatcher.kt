package com.github.sikv.photo.list.ui

import android.Manifest
import android.app.Activity
import android.os.Build
import android.view.View
import androidx.navigation.fragment.findNavController
import com.github.sikv.photos.common.DownloadService
import com.github.sikv.photos.common.PermissionManager
import com.github.sikv.photos.common.PhotoLoader
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.common.ui.OptionsBottomSheetDialog
import com.github.sikv.photos.common.ui.copyText
import com.github.sikv.photos.common.ui.openAppSettings
import com.github.sikv.photos.common.ui.openUrl
import com.github.sikv.photos.data.createShareIntent
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.args.SetWallpaperFragmentArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.github.sikv.photos.navigation.route.SetWallpaperRoute
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Deprecated("Use photo-usecase module")
class PhotoActionDispatcher(
    private val fragment: BaseFragment,
    private val downloadService: DownloadService,
    private val photoLoader: PhotoLoader,
    private val photoDetailsRoute: PhotoDetailsRoute,
    private val setWallpaperRoute: SetWallpaperRoute,
    private val onToggleFavorite: (Photo) -> Unit,
    private val onShowMessage: (String) -> Unit
) : OnPhotoActionListener {

    private val permissionManager = PermissionManager(fragment)

    private lateinit var photoPreviewPopup: PhotoPreviewPopup

    private fun getActivity(): Activity {
        return fragment.requireActivity()
    }

    override fun onPhotoAction(action: OnPhotoActionListener.Action, photo: Photo, view: View) {
        when (action) {
            OnPhotoActionListener.Action.CLICK -> {
                photoDetailsRoute.present(fragment.findNavController(), PhotoDetailsFragmentArguments(photo))
            }

            OnPhotoActionListener.Action.HOLD -> {
                if (!this::photoPreviewPopup.isInitialized) {
                    photoPreviewPopup = PhotoPreviewPopup(getActivity(), photoLoader)
                }
                photoPreviewPopup.show(view, photo)
            }

            OnPhotoActionListener.Action.RELEASE -> {
                photoPreviewPopup.dismiss()
            }

            OnPhotoActionListener.Action.PHOTOGRAPHER -> {
                photo.getPhotoPhotographerUrl()?.let { photographerUrl ->
                    getActivity().openUrl(photographerUrl)
                } ?: run {
                    getActivity().openUrl(photo.getPhotoShareUrl())
                }
            }

            OnPhotoActionListener.Action.OPTIONS -> {
                showOptionsDialog(photo)
            }

            OnPhotoActionListener.Action.FAVORITE -> {
                onToggleFavorite(photo)
            }

            OnPhotoActionListener.Action.SHARE -> {
                getActivity().startActivity(photo.createShareIntent())
            }

            OnPhotoActionListener.Action.DOWNLOAD -> {
                downloadPhoto(photo)
            }
        }
    }

    private fun downloadPhoto(photo: Photo) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // No need to request WRITE_EXTERNAL_STORAGE permission on Android 11 and higher
            downloadPhotoInternal(photo)
        } else {
            // Request WRITE_EXTERNAL_STORAGE permission on Android 10 and lower
            permissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
                if (granted) {
                    downloadPhotoInternal(photo)
                } else {
                    MaterialAlertDialogBuilder(getActivity())
                        .setTitle(R.string.storage_permission)
                        .setMessage(R.string.storage_permission_description)
                        .setPositiveButton(R.string.open_settings) { _, _ ->
                            getActivity().openAppSettings()
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show()
                }
            }
        }
    }

    private fun downloadPhotoInternal(photo: Photo) {
        downloadService.downloadPhoto(
            photoUrl = photo.getPhotoDownloadUrl(),
            notificationTitle = "Photos", // TODO Fix string.
            notificationDescription = "Downloading photo" // TODO Fix string.
        )
        onShowMessage(getActivity().getString(R.string.downloading_photo))
    }


    override fun onPhotoActionParentRelease() {
        if (this::photoPreviewPopup.isInitialized && photoPreviewPopup.isShown()) {
            photoPreviewPopup.dismiss()
        }
    }

    private fun showOptionsDialog(photo: Photo) {
        val options = listOf(
            getActivity().getString(R.string.set_wallpaper),
            getActivity().getString(R.string.copy_link)
        )

        val dialog = OptionsBottomSheetDialog.newInstance(options, null) { index ->
            when (index) {
                // Set Wallpaper
                0 -> {
                    setWallpaperRoute.present(fragment.childFragmentManager, SetWallpaperFragmentArguments(photo))
                }
                // Copy Link
                1 -> {
                    val label = getActivity().getString(R.string.photo_link)
                    val text = photo.getPhotoShareUrl()

                    getActivity().copyText(label, text)
                    onShowMessage(getActivity().getString(R.string.link_copied))
                }
            }
        }

        dialog.show(fragment.childFragmentManager)
    }
}
