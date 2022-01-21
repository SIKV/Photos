package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.databinding.FragmentSearchDashboardBinding
import com.github.sikv.photos.manager.VoiceInputManager
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoGridAdapter
import com.github.sikv.photos.ui.navigation.NavigationAnimation
import com.github.sikv.photos.util.applyStatusBarsInsets
import com.github.sikv.photos.util.scrollToTop
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.SearchDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchDashboardFragment : BaseFragment() {

    private var _binding: FragmentSearchDashboardBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var downloadService: DownloadService

    @Inject
    lateinit var glide: RequestManager

    private lateinit var voiceInputManager: VoiceInputManager

    private val viewModel: SearchDashboardViewModel by viewModels()

    private lateinit var recommendedPhotosAdapter: PhotoGridAdapter

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(
            fragment = this,
            downloadService = downloadService,
            glide = glide,
            onToggleFavorite = { /** Don't need to handle this action here. */ },
            onShowMessage = ::showMessage
        )
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.applyStatusBarsInsets()

        init()
        setListeners()

        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onScrollToTop() {
        binding.recommendedPhotosRecycler.scrollToTop()
    }

    private fun observe() {
        viewModel.recommendedPhotosLoadedEvent.observe(viewLifecycleOwner, { recommended ->
            binding.pullRefreshLayout.finishRefreshing()

            if (recommended.reset) {
                recommendedPhotosAdapter.clear()
            }

            if (recommended.photos.isEmpty() && !recommendedPhotosAdapter.hasItems()) {
                binding.recommendedPhotosRecycler.setVisibilityAnimated(View.GONE)
                binding.noResultsView.isVisible = true
            } else {
                binding.recommendedPhotosRecycler.setVisibilityAnimated(View.VISIBLE)
                binding.noResultsView.isVisible = false

                recommendedPhotosAdapter.addItems(
                    recommended.photos,
                    showLoadMoreOption = recommended.moreAvailable
                )
            }
        })
    }

    private fun showSearchFragment(searchText: String? = null) {
        navigation?.addFragment(SearchFragment.newInstance(searchText), animation = NavigationAnimation.NONE)
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

        binding.noResultsView.setActionButtonClickListener {
            viewModel.loadRecommendations(reset = true)
        }

        binding.pullRefreshLayout.onRefresh = {
            viewModel.loadRecommendations(reset = true)
        }
    }

    private fun init() {
        recommendedPhotosAdapter = PhotoGridAdapter(glide, photoActionDispatcher) {
            viewModel.loadRecommendations()
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.recommendedPhotosRecycler.layoutManager = layoutManager
        binding.recommendedPhotosRecycler.adapter = recommendedPhotosAdapter
    }
}
