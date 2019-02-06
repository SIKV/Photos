package com.github.sikv.photos.ui.fragment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.data.DataSourceState
import com.github.sikv.photos.data.SearchSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoPagedListAdapter
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_no_results_found.*

class SearchFragment : Fragment() {

    companion object {
        private const val SEARCH_SOURCE = "search_source"

        fun newInstance(searchSource: SearchSource): SearchFragment {
            val fragment = SearchFragment()

            val args = Bundle()
            args.putSerializable(SEARCH_SOURCE, searchSource)

            fragment.arguments = args

            return fragment
        }
    }

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    private var searchSource: SearchSource? = null
    private var photoAdapter: PhotoPagedListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchSource = arguments?.getSerializable(SEARCH_SOURCE) as? SearchSource
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun searchPhotos(text: String) {
        searchSource?.let { searchSource ->
            viewModel.searchPhotos(searchSource, text)?.observe(this, Observer {
                photoAdapter?.submitList(it)
            })

            viewModel.getSearchState(searchSource)?.observe(this, Observer { state ->
                state?.let(::handleState)
            })
        }
    }

    private fun handleState(state: DataSourceState) {
        if (state == DataSourceState.ERROR) {
            loadingErrorLayout.visibility = View.VISIBLE
        } else {
            loadingErrorLayout.visibility = View.GONE

            searchSource?.let { searchSource ->
                noResultsFoundLayout.visibility =
                        if (state != DataSourceState.LOADING && viewModel.searchListIsEmpty(searchSource)) View.VISIBLE
                        else View.GONE
            }
        }
    }

    private fun onPhotoClick(photo: Photo, view: View) {
        activity?.let { activity ->
            PhotoActivity.startActivity(activity, view, photo)
        }
    }

    private fun onPhotoLongClick(photo: Photo, view: View) {
        PhotoPreviewPopup.show(activity as Activity, searchRootLayout, photo)
    }

    private fun init() {
        photoAdapter = PhotoPagedListAdapter(Glide.with(this), ::onPhotoClick, ::onPhotoLongClick)
        searchRecycler.adapter = photoAdapter
    }
}