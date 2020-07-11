package com.github.sikv.photos.ui

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.PhotoItemClickSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.createShareIntent
import com.github.sikv.photos.ui.activity.BaseActivity
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.dialog.OptionsBottomSheetDialog
import com.github.sikv.photos.ui.dialog.SetWallpaperDialog
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.util.copyText
import com.github.sikv.photos.util.downloadPhotoAndSaveToPictures
import com.github.sikv.photos.util.openUrl

class PhotoClickDispatcher(
        private val fragment: Fragment,
        @IdRes private val rootLayoutId: Int,
        private val invertFavorite: (Photo) -> Unit
) {

    private fun getActivity(): Activity {
        return fragment.requireActivity()
    }

    private fun getRootLayout(): ViewGroup {
        return fragment.requireView().findViewById(rootLayoutId)
    }

    fun handlePhotoClick(clickSource: PhotoItemClickSource, photo: Photo, view: View) {
        when (clickSource) {
            PhotoItemClickSource.CLICK -> {
                PhotoActivity.startActivity(getActivity(), view, photo)
            }

            PhotoItemClickSource.LONG_CLICK -> {
                PhotoPreviewPopup().show(getActivity(), getRootLayout(), photo)
            }

            PhotoItemClickSource.PHOTOGRAPHER -> {
                photo.getPhotoPhotographerUrl()?.let { photographerUrl ->
                    getActivity().openUrl(photographerUrl)
                } ?: run {
                    getActivity().openUrl(photo.getSourceUrl())
                }
            }

            PhotoItemClickSource.OPTIONS -> {
                showOptionsDialog(photo)
            }

            PhotoItemClickSource.FAVORITE -> {
                invertFavorite(photo)
            }

            PhotoItemClickSource.SHARE -> {
                getActivity().startActivity(photo.createShareIntent())
            }

            PhotoItemClickSource.DOWNLOAD -> {
                (getActivity() as? BaseActivity)?.requestWriteExternalStoragePermission {
                    getActivity().downloadPhotoAndSaveToPictures(photo.getPhotoDownloadUrl())

                    App.instance.postGlobalMessage(getActivity().getString(R.string.downloading_photo))
                }
            }
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
                    SetWallpaperDialog.newInstance(photo).show(fragment.childFragmentManager)
                }

                // Copy Link
                1 -> {
                    val label = getActivity().getString(R.string.photo_link)
                    val text = photo.getPhotoShareUrl()

                    getActivity().copyText(label, text)
                    App.instance.postGlobalMessage(getActivity().getString(R.string.link_copied))
                }
            }
        }

        dialog.show(fragment.childFragmentManager)
    }
}