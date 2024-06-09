package com.github.sikv.photos.curated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CuratedPhotosFragment : Fragment() {

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
                        onGoToPhotoDetails = { photo ->
                            photoDetailsRoute.present(findNavController(), PhotoDetailsFragmentArguments(photo))
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
