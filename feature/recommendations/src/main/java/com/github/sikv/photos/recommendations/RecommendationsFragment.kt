package com.github.sikv.photos.recommendations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecommendationsFragment : BaseFragment() {

    private val viewModel: RecommendationsViewModel by viewModels()

    @Inject
    lateinit var photoDetailsRoute: PhotoDetailsRoute

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
                val uiState = viewModel.uiState.collectAsState()

                when (val state = uiState.value) {
                    is RecommendationsUiState.Loading -> {
                        RecommendationsLoadingScreen()
                    }
                    is RecommendationsUiState.Data -> {
                        RecommendationsScreen(
                            photos = state.photos,
                            onPhotoPressed = { photo ->
                                openPhotoDetails(photo)
                            },
                            isNextPageLoading = state.isNextPageLoading,
                            onLoadMore = {
                                viewModel.loadRecommendations()
                            },
                            onRefresh = {
                                viewModel.loadRecommendations(refresh = true)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun openPhotoDetails(photo: Photo) {
        photoDetailsRoute.present(navigation, PhotoDetailsFragmentArguments(photo))
    }
}
