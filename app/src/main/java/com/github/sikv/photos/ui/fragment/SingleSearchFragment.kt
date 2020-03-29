package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.enumeration.PhotoItemClickSource
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoPagedListAdapter
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.util.disableChangeAnimations
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_single_search.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_loading_list.*
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
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private lateinit var photoSource: PhotoSource
    private val photoAdapter = PhotoPagedListAdapter(::onPhotoClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoSource = arguments?.getSerializable(KEY_SEARCH_SOURCE) as PhotoSource
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_single_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosRecycler.adapter = photoAdapter
        photosRecycler.disableChangeAnimations()

        observe()
    }

    fun searchPhotos(text: String) {
        viewModel.searchPhotos(photoSource, text)?.observe(viewLifecycleOwner, Observer {
            photoAdapter.submitList(it)
        })

        observeLoadingState()
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

    private fun observeLoadingState() {
        viewModel.getSearchLoadingState(photoSource)?.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                DataSourceState.LOADING_INITIAL -> {
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    noResultsFoundLayout.setVisibilityAnimated(View.GONE, duration = 0)
                }

                DataSourceState.INITIAL_LOADING_DONE -> {
                    photosRecycler.setVisibilityAnimated(View.VISIBLE)
                    loadingListLayout.setVisibilityAnimated(View.GONE)
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)

                    if (viewModel.isSearchListEmpty(photoSource)) {
                        noResultsFoundLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    } else {
                        noResultsFoundLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    }
                }

                DataSourceState.ERROR -> {
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingErrorLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    noResultsFoundLayout.setVisibilityAnimated(View.GONE, duration = 0)
                }

                else -> { }
            }
        })
    }

    private fun onPhotoClick(clickSource: PhotoItemClickSource, photo: Photo, view: View) {
        when (clickSource) {
            PhotoItemClickSource.CLICK -> {
                PhotoActivity.startActivity(activity, view, photo)
            }

            PhotoItemClickSource.LONG_CLICK -> {
                PhotoPreviewPopup().show(activity, rootLayout, photo)
            }

            PhotoItemClickSource.FAVORITE -> {
                viewModel.invertFavorite(photo)
            }

            else -> { }
        }
    }
}