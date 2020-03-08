package com.github.sikv.photos.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoGridAdapter
import com.github.sikv.photos.ui.adapter.TagAdapter
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.viewmodel.SearchDashboardViewModel
import kotlinx.android.synthetic.main.fragment_search_dashboard.*

class SearchDashboardFragment : BaseFragment() {

    companion object {
        private const val REQUEST_CODE_SPEECH_RECOGNIZER = 125
    }

    private val viewModel: SearchDashboardViewModel by lazy {
        ViewModelProviders.of(this).get(SearchDashboardViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchTagsRecycler.visibility = View.GONE

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

    private fun onPhotoClick(photo: Photo, view: View) {
        PhotoActivity.startActivity(activity, view, photo)
    }

    private fun onPhotoLongClick(photo: Photo, view: View) {
        PhotoPreviewPopup.show(activity, rootLayout, photo)
    }

    private fun observe() {
        viewModel.searchTagsLiveData.observe(viewLifecycleOwner, Observer {
            searchTagsRecycler.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE

            searchTagsRecycler.adapter = TagAdapter(it) { tag ->
                showSearchFragment(searchText = tag.text)
            }
        })

        viewModel.suggestedPhotosLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = PhotoGridAdapter.create(it, ::onPhotoClick, ::onPhotoLongClick)

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            suggestedPhotosRecycler.layoutManager = layoutManager

            suggestedPhotosRecycler.adapter = adapter
        })
    }

    private fun showSearchFragment(searchText: String? = null) {
        childFragmentManager.beginTransaction()
                .replace(R.id.searchContainer, SearchFragment.newInstance(searchText))
                .addToBackStack(null)
                .commit()
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
    }
}