package com.github.sikv.photos.ui.activity

import android.animation.Animator
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
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.PhotoAdapter
import com.github.sikv.photos.util.AnimUtils
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*
import kotlinx.android.synthetic.main.popup_photo_preview.view.*


class MainActivity : AppCompatActivity() {

    companion object {

        private const val TOOLBAR_ELEVATION = 12f
        private const val ANIMATION_OFFSET = 200
    }


    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private var searchVisible = false

    private var photoAdapter: PhotoAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        init()
        initPhotoAdapter()

        viewModel.recentPhotos.observe(this, Observer<PagedList<Photo>> {
            photoAdapter?.submitList(it)
        })
    }

    override fun onBackPressed() {
        if (searchVisible) {
            closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    private fun searchPhotos(query: String) {
        // viewModel.recentPhotos.removeObservers(this)

        initPhotoAdapter()

        viewModel.searchPhotos(query)?.observe(this, Observer {
            photoAdapter?.submitList(it)
        })
    }

    private fun onPhotoClick(photo: Photo, view: View) {
        PhotoActivity.startActivity(this, view, photo)
    }

    private fun onPhotoLongClick(photo: Photo, view: View) {
        var photoPopupPreview: PopupWindow? = null
        val layout = layoutInflater.inflate(R.layout.popup_photo_preview, mainRootLayout, false)

        Glide.with(this)
                .load(photo.urls.small)
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

        mainSearchLayout.y = mainSearchLayout.y - ANIMATION_OFFSET

        mainSearchButton.setOnClickListener {
           openSearch()
        }

        mainCloseSearchButton.setOnClickListener {
            closeSearch()
        }

        mainSearchEdit.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchPhotos(textView.text.toString())
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    private fun initPhotoAdapter() {
        photoAdapter = PhotoAdapter(Glide.with(this), ::onPhotoClick, ::onPhotoLongClick)
        mainRecycler.adapter = photoAdapter
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
