package com.github.sikv.photos.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.sikv.photos.common.VoiceInputManager
import com.github.sikv.photos.config.FeatureFlag
import com.github.sikv.photos.config.FeatureFlagProvider
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.args.SearchFragmentArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.github.sikv.photos.navigation.route.SearchRoute
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchDashboardFragment : Fragment() {

    @Inject
    lateinit var featureFlagProvider: FeatureFlagProvider

    @Inject
    lateinit var photoDetailsRoute: PhotoDetailsRoute

    @Inject
    lateinit var searchRoute: SearchRoute

    private lateinit var voiceInputManager: VoiceInputManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        voiceInputManager = VoiceInputManager(requireActivity() as AppCompatActivity) { text ->
            if (!text.isNullOrBlank()) {
                showSearchFragment(text)
            }
        }
    }

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
                    SearchDashboardScreen(
                        onSearchClick = {
                            showSearchFragment()
                        },
                        onVoiceSearchClick = {
                            voiceInputManager.startSpeechRecognizer()
                        },
                        onPhotoClick = { photo ->
                            photoDetailsRoute.present(
                                findNavController(),
                                PhotoDetailsFragmentArguments(photo)
                            )
                        },
                        recommendationsEnabled = featureFlagProvider.isFeatureEnabled(FeatureFlag.RECOMMENDATIONS)
                    )
                }
            }
        }
    }

    private fun showSearchFragment(searchText: String? = null) {
        searchRoute.present(findNavController(), SearchFragmentArguments(searchText))
    }
}
