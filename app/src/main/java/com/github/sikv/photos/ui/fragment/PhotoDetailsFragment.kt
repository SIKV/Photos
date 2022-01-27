package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.createShareIntent
import com.github.sikv.photos.ui.FragmentArguments
import com.github.sikv.photos.ui.compose.PhotoDetailsScreen
import com.github.sikv.photos.ui.compose.state.PhotoViewState
import com.github.sikv.photos.ui.dialog.SetWallpaperDialog
import com.github.sikv.photos.ui.dialog.SetWallpaperFragmentArguments
import com.github.sikv.photos.ui.withArguments
import com.github.sikv.photos.util.openUrl
import com.github.sikv.photos.viewmodel.PhotoDetailsViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoDetailsFragmentArguments(
    val photo: Photo
) : FragmentArguments

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class PhotoDetailsFragment : BaseFragment() {

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
                ProvideWindowInsets {
                    Surface {
                        val viewState: State<PhotoViewState> =
                            viewModel.viewState.observeAsState(PhotoViewState.NoData)

                        when (val state = viewState.value) {
                            is PhotoViewState.Ready -> {
                                PhotoDetailsScreen(
                                    photo = state.photo,
                                    onBackPressed = { navigation?.backPressed() },
                                    isFavorite = state.isFavorite,
                                    onToggleFavorite = { viewModel.toggleFavorite() },
                                    onSharePressed = { sharePhoto(state.photo) },
                                    onDownloadPressed = { viewModel.downloadPhoto() },
                                    onSetWallpaperPressed = { setWallpaper(state.photo) },
                                    onAttributionPressed = { openAttribution(state.photo) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sharePhoto(photo: Photo) {
        startActivity(photo.createShareIntent())
    }

    private fun setWallpaper(photo: Photo) {
        SetWallpaperDialog()
            .withArguments(SetWallpaperFragmentArguments(photo))
            .show(childFragmentManager)
    }

    private fun openAttribution(photo: Photo) {
        requireContext().openUrl(photo.getPhotoShareUrl())
    }
}
