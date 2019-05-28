package com.github.sikv.photos.ui.fragment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.data.DataSourceState
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoPagedListAdapter
import com.github.sikv.photos.viewmodel.PhotosViewModel
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_no_results_found.*


class PhotosFragment : Fragment() {

    companion object {
        private const val TOOLBAR_ELEVATION = 12f
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initPhotoAdapter()

        currentSource = PhotoSource.UNSPLASH
    }

    private fun observePhotos() {
        viewModel.getPhotos(currentSource)?.observe(this, Observer<PagedList<Photo>> { pagedList ->
            photoAdapter?.submitList(pagedList)
        })
    }

    private fun observeState() {
        viewModel.getState(currentSource)?.observe(this, Observer { state ->
            photosLoadingLayout.visibility = View.GONE
            loadingErrorLayout.visibility = View.GONE

            when (state) {
                DataSourceState.LOADING_INITIAL -> {
                    photosLoadingLayout.visibility = View.VISIBLE
                }

                DataSourceState.ERROR -> {
                    loadingErrorLayout.visibility = View.VISIBLE
                }

                else -> {
                }
            }
        })
    }

    private fun onPhotoClick(photo: Photo, view: View) {
        PhotoActivity.startActivity(activity as Activity, view, photo)
    }

    private fun onPhotoLongClick(photo: Photo, view: View) {
    }

    private fun showSourcePopup() {
        val popup = PopupMenu(context!!, photosTitleLayout)
        popup.menuInflater.inflate(R.menu.menu_source, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itemSourceUnsplash -> {
                    currentSource = PhotoSource.UNSPLASH
                }

                R.id.itemSourcePexels -> {
                    currentSource = PhotoSource.PEXELS
                }
            }

            return@setOnMenuItemClickListener true
        }

        popup.show()
    }

    private fun init() {
        photosSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))

        photosSwipeRefreshLayout.setOnRefreshListener {
            photosSwipeRefreshLayout.isRefreshing = false

            // TODO Implement
        }

        photosRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                recyclerView.computeVerticalScrollOffset().let {
                    if (it > 0) {
                        ViewCompat.setElevation(photosAppBarLayout, TOOLBAR_ELEVATION)
                    } else {
                        ViewCompat.setElevation(photosAppBarLayout, 0.0f)
                    }
                }
            }
        })

        photosTitleLayout.setOnClickListener {
            showSourcePopup()
        }
    }

    private fun initPhotoAdapter() {
        photoAdapter = PhotoPagedListAdapter(Glide.with(this), ::onPhotoClick, ::onPhotoLongClick)
        photosRecycler.adapter = photoAdapter

        loadingErrorLayout.visibility = View.GONE
        noResultsFoundLayout.visibility = View.GONE
    }
}