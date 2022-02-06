package com.github.sikv.photos.ui

import android.app.Activity
import android.view.View
import com.github.sikv.photos.R
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.createShareIntent
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.activity.BaseActivity
import com.github.sikv.photos.ui.adapter.OnPhotoActionListener
import com.github.sikv.photos.ui.dialog.OptionsBottomSheetDialog
import com.github.sikv.photos.ui.dialog.PhotoPreviewPopup
import com.github.sikv.photos.ui.dialog.SetWallpaperDialog
import com.github.sikv.photos.ui.dialog.SetWallpaperFragmentArguments
import com.github.sikv.photos.ui.fragment.BaseFragment
import com.github.sikv.photos.ui.fragment.PhotoDetailsFragment
import com.github.sikv.photos.ui.fragment.PhotoDetailsFragmentArguments
import com.github.sikv.photos.ui.navigation.NavigationAnimation
import com.github.sikv.photos.util.copyText
import com.github.sikv.photos.util.openUrl

class PhotoActionDispatcher(
    private val fragment: BaseFragment,
    private val downloadService: DownloadService,
    private val photoLoader: PhotoLoader,
    private val onToggleFavorite: (Photo) -> Unit,
    private val onShowMessage: (String) -> Unit
) : OnPhotoActionListener {

    private lateinit var photoPreviewPopup: PhotoPreviewPopup

    private fun getActivity(): Activity {
        return fragment.requireActivity()
    }

    override fun onPhotoAction(action: OnPhotoActionListener.Action, photo: Photo, view: View) {
        when (action) {
            OnPhotoActionListener.Action.CLICK -> {
                val photoDetailsFragment = PhotoDetailsFragment()
                    .withArguments(PhotoDetailsFragmentArguments(photo))

                fragment.navigation?.addFragment(photoDetailsFragment,
                    animation = NavigationAnimation.SLIDE_V
                )
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
                (getActivity() as? BaseActivity)?.requestWriteExternalStoragePermission {
                    downloadService.downloadPhoto(photo.getPhotoDownloadUrl())
                    onShowMessage(getActivity().getString(R.string.downloading_photo))
                }
            }
        }
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
                    SetWallpaperDialog()
                        .withArguments(SetWallpaperFragmentArguments(photo))
                        .show(fragment.childFragmentManager)
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
