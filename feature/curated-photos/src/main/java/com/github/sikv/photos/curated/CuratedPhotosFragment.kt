package com.github.sikv.photos.curated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CuratedPhotosFragment : BaseFragment() {

    @Inject
    lateinit var photoDetailsRoute: PhotoDetailsRoute

    private val viewModel: CuratedPhotosViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                Mdc3Theme {
                    CuratedPhotosScreen(
                        viewModel = viewModel,
                        onOpenPhotoDetails = { photo ->
                            photoDetailsRoute.present(navigation, PhotoDetailsFragmentArguments(photo))
                        })
                }
            }
        }
    }
}
