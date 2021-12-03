package com.github.sikv.photos.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.databinding.FragmentSearchDashboardBinding
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoGridAdapter
import com.github.sikv.photos.util.scrollToTop
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.SearchDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchDashboardFragment : BaseFragment() {

    companion object {
        private const val RC_SPEECH_RECOGNIZER = 125
    }

    private var _binding: FragmentSearchDashboardBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var downloadService: DownloadService

    @Inject
    lateinit var glide: RequestManager

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

        init()
        setListeners()

        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SPEECH_RECOGNIZER && resultCode == Activity.RESULT_OK) {
            val spokenText =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                    results[0]
                }

            showSearchFragment(searchText = spokenText)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onScrollToTop() {
        binding.recommendedPhotosRecycler.scrollToTop()
    }

    private fun observe() {
        viewModel.recommendedPhotosLoadedEvent.observe(viewLifecycleOwner, Observer { recommended ->
            binding.pullRefreshLayout.finishRefreshing()

            if (recommended.reset) {
                recommendedPhotosAdapter.clear()
            }

            if (recommended.photos.isEmpty() && !recommendedPhotosAdapter.hasItems()) {
                binding.recommendedPhotosRecycler.setVisibilityAnimated(View.GONE)
                binding.noRecommendationsLayout.root.setVisibilityAnimated(View.VISIBLE)
            } else {
                binding.recommendedPhotosRecycler.setVisibilityAnimated(View.VISIBLE)
                binding.noRecommendationsLayout.root.setVisibilityAnimated(View.GONE)

                recommendedPhotosAdapter.addItems(
                    recommended.photos,
                    showLoadMoreOption = recommended.moreAvailable
                )
            }
        })
    }

    private fun showSearchFragment(searchText: String? = null) {
        navigation?.addFragment(SearchFragment.newInstance(searchText), withAnimation = false)
    }

    private fun showSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }

        startActivityForResult(intent, RC_SPEECH_RECOGNIZER)
    }

    private fun setListeners() {
        binding.searchButton.setOnClickListener {
            showSearchFragment()
        }

        binding.searchText.setOnClickListener {
            showSearchFragment()
        }

        binding.voiceSearchButton.setOnClickListener {
            showSpeechRecognizer()
        }

        binding.noRecommendationsLayout.refreshRecommendationsButton.setOnClickListener {
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
