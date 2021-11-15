package com.github.sikv.photos.ui

import android.app.Activity
import android.view.View
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.createShareIntent
import com.github.sikv.photos.ui.activity.BaseActivity
import com.github.sikv.photos.ui.adapter.OnPhotoActionListener
import com.github.sikv.photos.ui.dialog.OptionsBottomSheetDialog
import com.github.sikv.photos.ui.dialog.SetWallpaperDialog
import com.github.sikv.photos.ui.fragment.BaseFragment
import com.github.sikv.photos.ui.fragment.PhotoDetailsFragment
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.util.copyText
import com.github.sikv.photos.util.downloadPhotoAndSaveToPictures
import com.github.sikv.photos.util.openUrl

class PhotoActionDispatcher(
    private val fragment: BaseFragment,
    private val glide: RequestManager,
    private val invertFavorite: (Photo) -> Unit
) : OnPhotoActionListener {

    private lateinit var photoPreviewPopup: PhotoPreviewPopup

    private fun getActivity(): Activity {
        return fragment.requireActivity()
    }

    override fun onPhotoAction(action: OnPhotoActionListener.Action, photo: Photo, view: View) {
        when (action) {
            OnPhotoActionListener.Action.CLICK -> {
                fragment.navigation?.addFragment(PhotoDetailsFragment.newInstance(photo))
            }

            OnPhotoActionListener.Action.HOLD -> {
                if (!this::photoPreviewPopup.isInitialized) {
                    photoPreviewPopup = PhotoPreviewPopup(getActivity(), glide)
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
                invertFavorite(photo)
            }

            OnPhotoActionListener.Action.SHARE -> {
                getActivity().startActivity(photo.createShareIntent())
            }

            OnPhotoActionListener.Action.DOWNLOAD -> {
                (getActivity() as? BaseActivity)?.requestWriteExternalStoragePermission {
                    getActivity().downloadPhotoAndSaveToPictures(photo.getPhotoDownloadUrl())
                    App.instance.postGlobalMessage(getActivity().getString(R.string.downloading_photo))
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
