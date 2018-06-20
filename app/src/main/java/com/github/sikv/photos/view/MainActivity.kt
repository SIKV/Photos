package com.github.sikv.photos.view

import android.animation.Animator
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.adapter.PhotoAdapter
import com.github.sikv.photos.data.DataHandler
import com.github.sikv.photos.data.RecentPhotosDataSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.AnimUtils
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.viewmodel.PhotosViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    companion object {

        private const val TOOLBAR_ELEVATION = 12f
        private const val ANIMATION_OFFSET = 200
    }

    private var searchVisible = false

    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(Glide.with(this), { photo, view ->
            PhotoActivity.startActivity(this, view, photo)
        })
    }

    private val photosViewModel: PhotosViewModel by lazy {
        ViewModelProviders.of(this).get(PhotosViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        init()

        val recentPhotosDataSource = RecentPhotosDataSource(DataHandler.INSTANCE.photosHandler)

        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(10)
                .build()

        val pagedList = PagedList.Builder<Int, Photo>(recentPhotosDataSource, pagedListConfig)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(object : Executor {

                    private val mHandler = Handler(Looper.getMainLooper())

                    override fun execute(p0: Runnable?) {
                        mHandler.post(p0)
                    }

                })
                .build()

        photoAdapter.submitList(pagedList)
    }

    override fun onBackPressed() {
        if (searchVisible) {
            closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    private fun init() {
        mainRecycler.adapter = photoAdapter

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

        mainSearchLayout.y = mainSearchLayout.y - ANIMATION_OFFSET

        mainSearchButton.setOnClickListener {
           openSearch()
        }

        mainCloseSearchButton.setOnClickListener {
            closeSearch()
        }
    }

    private fun openSearch() {
        AnimUtils.animateX(mainFavoritesButton, mainFavoritesButton.x - ANIMATION_OFFSET)
        AnimUtils.animateX(mainSearchButton, mainSearchButton.x + ANIMATION_OFFSET)

        AnimUtils.animateY(mainSearchLayout, mainSearchLayout.y + ANIMATION_OFFSET, AnimUtils.DURATION_NORMAL,
                object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        mainSearchEdit.requestFocus()
                        Utils.showSoftInput(this@MainActivity, mainSearchEdit)
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        mainTitleText.visibility = View.INVISIBLE
                    }
                })

        searchVisible = true
    }

    private fun closeSearch() {
        AnimUtils.animateX(mainFavoritesButton, mainFavoritesButton.x + ANIMATION_OFFSET)
        AnimUtils.animateX(mainSearchButton, mainSearchButton.x - ANIMATION_OFFSET)

        AnimUtils.animateY(mainSearchLayout, mainSearchLayout.y - ANIMATION_OFFSET, AnimUtils.DURATION_NORMAL,
                object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        mainTitleText.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                        Utils.hideSoftInput(this@MainActivity, mainSearchEdit)
                    }
                })

        searchVisible = false
    }
}
