package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoPagedListAdapter
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.*
import com.github.sikv.photos.viewmodel.PhotosViewModel
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_loading_list.*

class PhotosFragment : BaseFragment() {

    companion object {
        private const val KEY_CURRENT_SPAN_COUNT = "key_current_span_count"
    }

    private val viewModel: PhotosViewModel by lazy {
        ViewModelProvider(this).get(PhotosViewModel::class.java)
    }

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(this) { photo ->
            viewModel.invertFavorite(photo)
        }
    }

    private val photoAdapter = PhotoPagedListAdapter(photoActionDispatcher)

    private var currentSpanCount: Int = SPAN_COUNT_LIST
        set(value) {
            field = value

            val itemLayoutType = PhotoItemLayoutType.findBySpanCount(field)

            photoAdapter.setItemLayoutType(itemLayoutType)
            photosRecycler.setItemLayoutType(itemLayoutType)

            setMenuItemVisibility(R.id.itemViewList, field == SPAN_COUNT_GRID)
            setMenuItemVisibility(R.id.itemViewGrid, field == SPAN_COUNT_LIST)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(R.string.app_name)

        photosRecycler.adapter = photoAdapter
        photosRecycler.disableChangeAnimations()

        if (savedInstanceState != null) {
            currentSpanCount = savedInstanceState.getInt(KEY_CURRENT_SPAN_COUNT, SPAN_COUNT_LIST)
        } else {
            currentSpanCount = SPAN_COUNT_LIST
        }

        observe()
        observeLoadingState()
    }

    override fun onCreateToolbar(): FragmentToolbar? {
        return FragmentToolbar.Builder()
                .withId(R.id.toolbar)
                .withMenu(R.menu.menu_photos)
                .withMenuItems(
                        listOf(
                                R.id.itemViewList,
                                R.id.itemViewGrid
                        ),
                        listOf(
                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        currentSpanCount = SPAN_COUNT_LIST
                                        return true
                                    }
                                },

                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        currentSpanCount = SPAN_COUNT_GRID
                                        return true
                                    }
                                }
                        )
                )
                .build()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_CURRENT_SPAN_COUNT, currentSpanCount)
    }

    override fun onScrollToTop() {
        photosRecycler.scrollToTop()
    }

    private fun observe() {
        viewModel.getPexelsCuratedPhotos()?.observe(viewLifecycleOwner, Observer<PagedList<Photo>> { pagedList ->
            photoAdapter.submitList(pagedList)
        })

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
        viewModel.getLoadingState()?.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                DataSourceState.LOADING_INITIAL -> {
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                }

                DataSourceState.INITIAL_LOADING_DONE -> {
                    photosRecycler.setVisibilityAnimated(View.VISIBLE)
                    loadingListLayout.setVisibilityAnimated(View.GONE)
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)
                }

                DataSourceState.NEXT_DONE -> {
                    // In some cases INITIAL_LOADING_DONE is not being called
                    if (photosRecycler.visibility != View.VISIBLE) {
                        photosRecycler.setVisibilityAnimated(View.VISIBLE)
                        loadingListLayout.setVisibilityAnimated(View.GONE)
                        loadingErrorLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    }
                }

                DataSourceState.ERROR -> {
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingErrorLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                }

                else -> { }
            }
        })
    }
}