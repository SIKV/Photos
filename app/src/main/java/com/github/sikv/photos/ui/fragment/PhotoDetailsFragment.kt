package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.createShareIntent
import com.github.sikv.photos.ui.compose.PhotoDetailsScreen
import com.github.sikv.photos.ui.dialog.SetWallpaperDialog
import com.github.sikv.photos.ui.state.PhotoState
import com.github.sikv.photos.util.downloadPhotoAndSaveToPictures
import com.github.sikv.photos.util.openUrl
import com.github.sikv.photos.viewmodel.PhotoDetailsViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class PhotoDetailsFragment : BaseFragment() {

    companion object {
        fun newInstance(photo: Photo): PhotoDetailsFragment = PhotoDetailsFragment()
            .apply { arguments = bundleOf(Photo.KEY to photo) }
    }

    private val viewModel: PhotoDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContent {
            MdcTheme {
                Surface {
                    val uiState: PhotoState? by viewModel.uiState.observeAsState()

                    uiState?.let { photoState ->
                        PhotoDetailsScreen(
                            photo = photoState.photo,
                            onBackPressed = { navigation?.backPressed() },
                            isFavorite = photoState.isFavorite,
                            onToggleFavorite = { viewModel.toggleFavorite() },
                            onSharePressed = { sharePhoto(photoState.photo) },
                            onDownloadPressed = { downloadPhoto(photoState.photo) },
                            onSetWallpaperPressed = { setWallpaper(photoState.photo) },
                            onAttributionPressed = { openAttribution(photoState.photo) }
                        )
                    }
                }
            }
        }
    }

    private fun sharePhoto(photo: Photo) {
        startActivity(photo.createShareIntent())
    }

    private fun downloadPhoto(photo: Photo) {
        requireContext().downloadPhotoAndSaveToPictures(photo.getPhotoDownloadUrl())
        App.instance.postGlobalMessage(App.instance.getString(R.string.downloading_photo))

    }

    private fun setWallpaper(photo: Photo) {
        SetWallpaperDialog
            .newInstance(photo)
            .show(childFragmentManager)
    }

    private fun openAttribution(photo: Photo) {
        requireContext().openUrl(photo.getPhotoShareUrl())
    }
}
