package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.compose.RecommendationsLoadingScreen
import com.github.sikv.photos.ui.compose.RecommendationsScreen
import com.github.sikv.photos.ui.navigation.NavigationAnimation
import com.github.sikv.photos.ui.withArguments
import com.github.sikv.photos.viewmodel.RecommendationsUiState
import com.github.sikv.photos.viewmodel.RecommendationsViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendationsFragment : BaseFragment() {

    private val viewModel: RecommendationsViewModel by viewModels()

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
                // TODO
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
        val photoDetailsFragment = PhotoDetailsFragment()
            .withArguments(PhotoDetailsFragmentArguments(photo))

        navigation?.addFragment(photoDetailsFragment,
            animation = NavigationAnimation.SLIDE_V
        )
    }
}
