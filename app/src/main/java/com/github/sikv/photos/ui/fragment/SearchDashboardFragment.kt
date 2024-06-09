package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.sikv.photos.R
import com.github.sikv.photos.common.VoiceInputManager
import com.github.sikv.photos.common.ui.applyStatusBarsInsets
import com.github.sikv.photos.config.FeatureFlag
import com.github.sikv.photos.config.FeatureFlagProvider
import com.github.sikv.photos.databinding.FragmentSearchDashboardBinding
import com.github.sikv.photos.navigation.args.SearchFragmentArguments
import com.github.sikv.photos.navigation.route.SearchRoute
import com.github.sikv.photos.recommendations.RecommendationsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Move to a separate module.

@AndroidEntryPoint
class SearchDashboardFragment : Fragment() {

    @Inject
    lateinit var featureFlagProvider: FeatureFlagProvider

    @Inject
    lateinit var searchRoute: SearchRoute

    private lateinit var voiceInputManager: VoiceInputManager

    private var _binding: FragmentSearchDashboardBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        _binding = FragmentSearchDashboardBinding.inflate(inflater, container, false)

        if (savedInstanceState == null) {
            if (featureFlagProvider.isFeatureEnabled(FeatureFlag.RECOMMENDATIONS)) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.contentContainer, RecommendationsFragment())
                    .commit()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.applyStatusBarsInsets()

        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSearchFragment(searchText: String? = null) {
        searchRoute.present(findNavController(), SearchFragmentArguments(searchText))
    }

    private fun setListeners() {
        binding.searchButton.setOnClickListener {
            showSearchFragment()
        }

        binding.searchText.setOnClickListener {
            showSearchFragment()
        }

        binding.voiceSearchButton.setOnClickListener {
            voiceInputManager.startSpeechRecognizer()
        }
    }
}
