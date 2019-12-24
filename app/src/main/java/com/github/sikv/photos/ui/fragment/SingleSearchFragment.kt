package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.sikv.photos.R
import com.github.sikv.photos.data.DataSourceState
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoPagedListAdapter
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_single_search.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_no_results_found.*

class SingleSearchFragment : Fragment() {

    companion object {
        private const val KEY_SEARCH_SOURCE = "key_search_source"

        fun newInstance(photoSource: PhotoSource): SingleSearchFragment {
            val args = Bundle()
            args.putSerializable(KEY_SEARCH_SOURCE, photoSource)

            val fragment = SingleSearchFragment()
            fragment.arguments = args

            return fragment
        }
    }

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    private var photoSource: PhotoSource? = null
    private var photoAdapter = PhotoPagedListAdapter(::onPhotoClick, ::onPhotoLongClick, ::onPhotoFavoriteClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoSource = arguments?.getSerializable(KEY_SEARCH_SOURCE) as? PhotoSource
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_single_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchRecycler.adapter = photoAdapter

        observe()
    }

    private fun observe() {
        viewModel.favoriteChangedEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { photo ->
                photoAdapter.notifyPhotoChanged(photo)
            }
        })

        viewModel.favoritesChangedEvent.observe(viewLifecycleOwner, Observer {
            if (it.canHandle()) {
                photoAdapter.notifyDataSetChanged()
            }
        })
    }

    fun searchPhotos(text: String) {
        photoSource?.let { searchSource ->
            viewModel.searchPhotos(searchSource, text)?.observe(viewLifecycleOwner, Observer {
                photoAdapter.submitList(it)
            })

            viewModel.getSearchState(searchSource)?.observe(viewLifecycleOwner, Observer { state ->
                state?.let(::handleState)
            })
        }
    }

    private fun onPhotoClick(photo: Photo, view: View) {
        PhotoActivity.startActivity(activity!!, view, photo)
    }

    private fun onPhotoLongClick(photo: Photo, view: View) {
        PhotoPreviewPopup.show(activity!!, rootLayout, photo)
    }

    private fun onPhotoFavoriteClick(photo: Photo) {
        viewModel.invertFavorite(photo)
    }

    private fun handleState(state: DataSourceState) {
        if (state == DataSourceState.ERROR) {
            loadingErrorLayout.visibility = View.VISIBLE
        } else {
            loadingErrorLayout.visibility = View.GONE

            photoSource?.let { searchSource ->
                noResultsFoundLayout.visibility =
                        if (state != DataSourceState.LOADING_NEXT && viewModel.isSearchListEmpty(searchSource)) View.VISIBLE
                        else View.GONE
            }
        }
    }
}