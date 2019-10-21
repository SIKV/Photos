package com.github.sikv.photos.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.github.sikv.photos.R
import com.github.sikv.photos.data.DataSourceState
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoPagedListAdapter
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.SPAN_COUNT_GRID
import com.github.sikv.photos.util.SPAN_COUNT_LIST
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.PhotosViewModel
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlinx.android.synthetic.main.layout_loading_error.*

class PhotosFragment : BaseFragment() {

    companion object {
        private const val KEY_CURRENT_SOURCE = "key_current_source"
        private const val KEY_CURRENT_SPAN_COUNT = "key_current_span_count"
    }

    private val viewModel: PhotosViewModel by lazy {
        ViewModelProviders.of(this).get(PhotosViewModel::class.java)
    }

    private var photoAdapter: PhotoPagedListAdapter? = null

    private var currentSource: PhotoSource = PhotoSource.UNSPLASH
        set(value) {
            field = value

            when (field) {
                PhotoSource.UNSPLASH -> {
                    photosSourceText.setText(R.string.unsplash)
                }

                PhotoSource.PEXELS -> {
                    photosSourceText.setText(R.string.pexels)
                }
            }

            observePhotos()
            observeState()
        }

    private var currentSpanCount: Int = SPAN_COUNT_LIST
        set(value) {
            field = value

            setRecyclerLayoutManager(value)

            setMenuItemVisibility(R.id.itemViewList, field == SPAN_COUNT_GRID)
            setMenuItemVisibility(R.id.itemViewGrid, field == SPAN_COUNT_LIST)
        }

    private lateinit var photoSourceDialog: OptionsBottomSheetDialogFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListeners()

        if (savedInstanceState != null) {
            savedInstanceState.getString(KEY_CURRENT_SOURCE)?.let { currentSourceName ->
                currentSource = PhotoSource.valueOf(currentSourceName)
            }

            currentSpanCount = savedInstanceState.getInt(KEY_CURRENT_SPAN_COUNT, SPAN_COUNT_LIST)

        } else {
            currentSource = PhotoSource.UNSPLASH
            currentSpanCount = SPAN_COUNT_LIST
        }
    }

    override fun onCreateToolbar(): FragmentToolbar? {
        return FragmentToolbar.Builder()
                .withId(R.id.photosToolbar)
                .withMenu(R.menu.menu_photos)
                .withMenuItems(
                        listOf(
                                R.id.itemViewList,
                                R.id.itemViewGrid),
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

        outState.putString(KEY_CURRENT_SOURCE, currentSource.name)
        outState.putInt(KEY_CURRENT_SPAN_COUNT, currentSpanCount)
    }

    private fun observePhotos() {
        viewModel.getPhotos(currentSource)?.observe(this, Observer<PagedList<Photo>> { pagedList ->
            photoAdapter?.submitList(pagedList)
        })
    }

    private fun observeState() {
        viewModel.getState(currentSource)?.observe(this, Observer { state ->
            when (state) {
                DataSourceState.LOADING_INITIAL -> {
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    photosLoadingLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                }

                DataSourceState.INITIAL_LOADING_DONE -> {
                    photosRecycler.setVisibilityAnimated(View.VISIBLE)
                    photosLoadingLayout.setVisibilityAnimated(View.GONE)
                }

                DataSourceState.NEXT_DONE -> {
                    // In some cases INITIAL_LOADING_DONE is not being called
                    if (photosRecycler.visibility != View.VISIBLE) {
                        photosRecycler.setVisibilityAnimated(View.VISIBLE)
                        photosLoadingLayout.setVisibilityAnimated(View.GONE)
                    }
                }

                DataSourceState.ERROR -> {
                    loadingErrorLayout.setVisibilityAnimated(View.VISIBLE)
                }

                else -> { }
            }
        })
    }

    private fun onPhotoClick(photo: Photo, view: View) {
        PhotoActivity.startActivity(activity as Activity, view, photo)
    }

    private fun onPhotoLongClick(photo: Photo, view: View) {
    }

    private fun setRecyclerLayoutManager(spanCount: Int) {
        photosRecycler.layoutManager = GridLayoutManager(context, spanCount)
    }

    private fun init() {
        photoAdapter = PhotoPagedListAdapter(::onPhotoClick, ::onPhotoLongClick)
        photosRecycler.adapter = photoAdapter

        photosSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))

        createPhotoSourceDialog()
    }

    private fun setListeners() {
        photosSwipeRefreshLayout.setOnRefreshListener {
            photosSwipeRefreshLayout.isRefreshing = false

            // TODO Implement
        }

        photosTitleLayout.setOnClickListener {
            photoSourceDialog.show(childFragmentManager)
        }
    }

    private fun createPhotoSourceDialog() {
        photoSourceDialog = OptionsBottomSheetDialogFragment.newInstance(
                listOf(
                        getString(R.string.unsplash),
                        getString(R.string.pexels)

                )) { index ->

            when (index) {
                0 -> {
                    currentSource = PhotoSource.UNSPLASH
                }

                1 -> {
                    currentSource = PhotoSource.PEXELS
                }
            }
        }
    }
}