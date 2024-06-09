package com.github.sikv.photos.recommendations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.google.accompanist.themeadapter.material3.Mdc3Theme
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
            Mdc3Theme {
                val uiState = viewModel.uiState.collectAsState()

                RecommendationsScreen(
                    isRefreshing = uiState.value.isLoading,
                    photos = uiState.value.photos,
                    onPhotoPressed = { photo ->
                        openPhotoDetails(photo)
                    },
                    isNextPageLoading = uiState.value.isNextPageLoading,
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

    private fun openPhotoDetails(photo: Photo) {
        photoDetailsRoute.present(this.findNavController(), PhotoDetailsFragmentArguments(photo))
    }
}
