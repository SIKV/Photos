package com.github.sikv.photos.view

import android.animation.Animator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.sikv.photos.R
import com.github.sikv.photos.adapter.PhotoAdapter
import com.github.sikv.photos.util.AnimUtils
import com.github.sikv.photos.viewmodel.PhotosViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*


class MainActivity : AppCompatActivity() {

    private val ANIMATION_OFFSET = 200

    private var searchVisible = false

    private val photoAdapter = PhotoAdapter({
        PhotoActivity.startActivity(this, it)
    })

    private val viewModel: PhotosViewModel by lazy {
        ViewModelProviders.of(this).get(PhotosViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        init()

        viewModel.photos.observe(this, Observer {
            it?.let {
                photoAdapter.setItems(it)
            }
        })
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
                        ViewCompat.setElevation(mainAppBarLayout, 12.0f)
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
                    }
                })

        searchVisible = false
    }
}
