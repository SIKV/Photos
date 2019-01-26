package com.github.sikv.photos.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.PhotoAdapter
import com.github.sikv.photos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_no_results_found.*
import kotlinx.android.synthetic.main.popup_photo_preview.view.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TOOLBAR_ELEVATION = 12f
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private var photoAdapter: PhotoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        init()
        recentPhotos()
    }

    private fun recentPhotos() {
        initPhotoAdapter()

        viewModel.recentPhotos.observe(this, Observer<PagedList<Photo>> {
            photoAdapter?.submitList(it)
        })
    }

    private fun onPhotoClick(photo: Photo, view: View) {
//        PhotoActivity.startActivity(this, view, unsplashPhoto)
    }

    private fun onPhotoLongClick(photo: Photo, view: View) {
        var photoPopupPreview: PopupWindow? = null
        val layout = layoutInflater.inflate(R.layout.popup_photo_preview, mainRootLayout, false)

        Glide.with(this)
                .load(photo.getSmallUrl())
                .into(layout.photoPreviewImage)

        layout.setOnClickListener {
            photoPopupPreview?.dismiss()
        }

        photoPopupPreview = PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        photoPopupPreview.showAtLocation(mainRootLayout, Gravity.CENTER, 0, 0)
    }

    private fun init() {
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                recyclerView?.computeVerticalScrollOffset()?.let {
                    if (it > 0) {
                        ViewCompat.setElevation(mainAppBarLayout, TOOLBAR_ELEVATION)
                    } else {
                        ViewCompat.setElevation(mainAppBarLayout, 0.0f)
                    }
                }
            }
        })

        mainSearchButton.setOnClickListener {
            SearchActivity.startActivity(this)
        }

        mainFavoritesButton.setOnClickListener {
            FavoritesActivity.startActivity(this)
        }
    }

    private fun initPhotoAdapter() {
        photoAdapter = PhotoAdapter(Glide.with(this), ::onPhotoClick, ::onPhotoLongClick)
        mainRecycler.adapter = photoAdapter

        loadingErrorLayout.visibility = View.GONE
        noResultsFoundLayout.visibility = View.GONE
    }
}