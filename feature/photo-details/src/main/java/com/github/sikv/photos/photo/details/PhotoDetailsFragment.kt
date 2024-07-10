package com.github.sikv.photos.photo.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.sikv.photo.usecase.PhotoActionsUseCase
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhotoDetailsFragment : Fragment() {

    @Inject
    lateinit var photoActionsUseCase: PhotoActionsUseCase

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
            Mdc3Theme {
                Surface {
                    PhotoDetailsScreen(
                        onBackClick = {
                            findNavController().popBackStack()
                        },
                        onPhotoAttributionClick = photoActionsUseCase::photoAttributionClick,
                        onSharePhotoClick = { photo ->
                            photoActionsUseCase.sharePhoto(requireNotNull(activity), photo)
                        },
                        onDownloadPhotoClick = { photo ->
                            photoActionsUseCase.downloadPhoto(requireNotNull(activity), photo)
                        },
                        onSetWallpaperClick = { photo ->
                            photoActionsUseCase.setWallpaper(requireNotNull(activity), photo)
                        }
                    )
                }
            }
        }
    }
}
