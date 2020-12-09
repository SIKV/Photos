package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoPagingAdapter
import com.github.sikv.photos.util.disableChangeAnimations
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.SearchViewModel
import com.github.sikv.photos.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_single_search.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_loading_list.*
import kotlinx.android.synthetic.main.layout_no_results_found.*
import javax.inject.Inject

class SingleSearchFragment : BaseFragment() {

    companion object {
        private const val KEY_PHOTO_SOURCE_ID = "photoSourceId"

        fun newInstance(photoSource: PhotoSource): SingleSearchFragment {
            val args = Bundle()
            args.putInt(KEY_PHOTO_SOURCE_ID, photoSource.id)

            val fragment = SingleSearchFragment()
            fragment.arguments = args

            return fragment
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
    }

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(this) { photo ->
            viewModel.invertFavorite(photo)
        }
    }

    private var photoSource: PhotoSource? = null

    private val photoAdapter = PhotoPagingAdapter(photoActionDispatcher)

    init {
        App.instance.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt(KEY_PHOTO_SOURCE_ID)?.let { photoSourceId ->
            photoSource = PhotoSource.findById(photoSourceId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_single_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosRecycler.adapter = photoAdapter
        photosRecycler.disableChangeAnimations()

        tryAgainButton.setOnClickListener {
            photoAdapter.retry()
        }

        initAdapter()
        observe()
    }

    fun searchPhotos(text: String) {
        photoSource?.let { photoSource ->
            viewModel.searchPhotos(photoSource, text)?.observe(viewLifecycleOwner, Observer {
                photoAdapter.submitData(lifecycle, it)
            })
        }
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

    private fun initAdapter() {
        photoAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.NotLoading -> {
                    photosRecycler.setVisibilityAnimated(View.VISIBLE)
                    loadingListLayout.setVisibilityAnimated(View.GONE)
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)

                    if (photoAdapter.itemCount == 0) {
                        noResultsFoundLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    } else {
                        noResultsFoundLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    }
                }

                is LoadState.Loading -> {
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    noResultsFoundLayout.setVisibilityAnimated(View.GONE, duration = 0)
                }

                is LoadState.Error -> {
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingErrorLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    noResultsFoundLayout.setVisibilityAnimated(View.GONE, duration = 0)
                }
            }
        }
    }
}