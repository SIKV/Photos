package com.github.sikv.photos.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.sikv.photo.usecase.PhotoActionsUseCase
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    @Inject
    lateinit var photoDetailsRoute: PhotoDetailsRoute

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
                    SearchScreen(
                        onBackClick = {
                            findNavController().popBackStack()
                        },
                        onPhotoClick = { photo ->
                            photoDetailsRoute.present(findNavController(), PhotoDetailsFragmentArguments(photo))
                        },
                        onPhotoAttributionClick = { photo ->
                            photoActionsUseCase.photoAttributionClick(photo)
                        },
                        onPhotoActionsClick = { photo ->
                            photoActionsUseCase.openMoreActions(requireNotNull(activity), photo)
                        },
                        onSharePhotoClick = { photo ->
                            photoActionsUseCase.sharePhoto(requireNotNull(activity), photo)
                        },
                        onDownloadPhotoClick = { photo ->
                            photoActionsUseCase.downloadPhoto(requireNotNull(activity), photo)
                        }
                    )
                }
            }
        }
    }
}
