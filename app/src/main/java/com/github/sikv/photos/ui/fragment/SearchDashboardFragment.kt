package com.github.sikv.photos.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoGridAdapter
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.SearchDashboardViewModel
import kotlinx.android.synthetic.main.fragment_search_dashboard.*
import kotlinx.android.synthetic.main.layout_no_recommendations.*

class SearchDashboardFragment : BaseFragment() {

    companion object {
        private const val REQUEST_CODE_SPEECH_RECOGNIZER = 125
    }

    private val viewModel: SearchDashboardViewModel by lazy {
        ViewModelProvider(this).get(SearchDashboardViewModel::class.java)
    }

    private lateinit var recommendedPhotosAdapter: PhotoGridAdapter

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(this) {
            // Don't need to handle [Favorite] action here.
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListeners()

        observe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SPEECH_RECOGNIZER && resultCode == Activity.RESULT_OK) {
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                results[0]
            }

            showSearchFragment(searchText = spokenText)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun observe() {
        viewModel.recommendedPhotosLoadedEvent.observe(viewLifecycleOwner, Observer { recommended ->
            pullRefreshLayout.finishRefreshing()

            if (recommended.reset) {
                recommendedPhotosAdapter.clear()
            }

            if (recommended.photos.isEmpty() && !recommendedPhotosAdapter.hasItems()) {
                recommendedPhotosRecycler.setVisibilityAnimated(View.GONE)
                noRecommendationsLayout.setVisibilityAnimated(View.VISIBLE)
            } else {
                recommendedPhotosRecycler.setVisibilityAnimated(View.VISIBLE)
                noRecommendationsLayout.setVisibilityAnimated(View.GONE)

                recommendedPhotosAdapter.addItems(recommended.photos, showLoadMoreOption = recommended.moreAvailable)
            }
        })
    }

    private fun showSearchFragment(searchText: String? = null) {
        navigation?.addFragment(SearchFragment.newInstance(searchText), withAnimation = false)
    }

    private fun showSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }

        startActivityForResult(intent, REQUEST_CODE_SPEECH_RECOGNIZER)
    }

    private fun setListeners() {
        searchButton.setOnClickListener {
            showSearchFragment()
        }

        searchText.setOnClickListener {
            showSearchFragment()
        }

        voiceSearchButton.setOnClickListener {
            showSpeechRecognizer()
        }

        refreshRecommendationsButton.setOnClickListener {
            viewModel.loadRecommendations(reset = true)
        }

        pullRefreshLayout.onRefresh = {
            viewModel.loadRecommendations(reset = true)
        }
    }

    private fun init() {
        recommendedPhotosAdapter = PhotoGridAdapter(photoActionDispatcher) {
            viewModel.loadRecommendations()
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recommendedPhotosRecycler.layoutManager = layoutManager
        recommendedPhotosRecycler.adapter = recommendedPhotosAdapter
    }
}