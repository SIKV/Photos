package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.common.ui.openUrl
import com.github.sikv.photos.data.createShareIntent
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.navigation.args.SetWallpaperFragmentArguments
import com.github.sikv.photos.navigation.route.SetWallpaperRoute
import com.github.sikv.photos.ui.screen.PhotoDetailsScreen
import com.github.sikv.photos.viewmodel.PhotoDetailsViewModel
import com.github.sikv.photos.viewmodel.PhotoUiState
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class PhotoDetailsFragment : BaseFragment() {

    @Inject
    lateinit var setWallpaperRoute: SetWallpaperRoute

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
                        val uiState = viewModel.uiState.collectAsState()

                        when (val state = uiState.value) {
                            PhotoUiState.NoData -> {
                                // Don't need to handle NoData state.
                            }
                            is PhotoUiState.Ready -> {
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
        setWallpaperRoute.present(childFragmentManager, SetWallpaperFragmentArguments(photo))
    }

    private fun openAttribution(photo: Photo) {
        requireContext().openUrl(photo.getPhotoShareUrl())
    }
}
