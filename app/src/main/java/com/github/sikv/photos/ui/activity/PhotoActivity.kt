package com.github.sikv.photos.ui.activity

import android.annotation.TargetApi
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.text.style.ClickableSpan
import android.transition.ChangeBounds
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.viewmodel.PhotoViewModel
import com.github.sikv.photos.viewmodel.PhotoViewModelFactory
import kotlinx.android.synthetic.main.activity_photo.*


class PhotoActivity : AppCompatActivity() {

    private lateinit var viewModel: PhotoViewModel

    private var favoriteMenuItemIcon: Int? = null

    companion object {

        private const val FAVORITE_ANIMATION_DURATION = 200L

        private const val EXTRA_PHOTO = "photo"

        fun startActivity(activity: Activity, transitionView: View, photo: Photo) {
            val intent = Intent(activity, PhotoActivity::class.java)
            intent.putExtra(EXTRA_PHOTO, photo)

            val transitionName = activity.getString(R.string.transition_photo)

            val options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity, transitionView, transitionName)

            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo)
        setTransitionAnimationDuration()

        val photo: Photo = intent.getParcelableExtra(EXTRA_PHOTO)

        viewModel = ViewModelProviders.of(this, PhotoViewModelFactory(application, photo))
                .get(PhotoViewModel::class.java)

        init(photo)
        initListeners()
        initObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()

        hideViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        favoriteMenuItemIcon?.let {
            menu?.findItem(R.id.itemFavorite)?.setIcon(it)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.itemFavorite -> {
                viewModel.favorite()

                val itemView = findViewById<View>(R.id.itemFavorite)

                val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)

                scaleAnimation.duration = FAVORITE_ANIMATION_DURATION
                itemView.startAnimation(scaleAnimation)

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun init(photo: Photo) {
        setSupportActionBar(photoToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val authorName = photo.user.name
        val source = getString(R.string.unsplash)

        photoAuthorText.text = String.format(getString(R.string.photo_by_s_on_s), authorName, source)

        Utils.makeUnderlineBold(photoAuthorText, arrayOf(authorName, source))

        Utils.makeClickable(photoAuthorText, arrayOf(authorName, source),
                arrayOf(
                        object : ClickableSpan() {
                            override fun onClick(p0: View?) {
                                viewModel.openAuthorUrl()
                            }
                        },
                        object : ClickableSpan() {
                            override fun onClick(p0: View?) {
                                viewModel.openPhotoSource()
                            }
                        }
                ))

        adjustMargins()
    }

    private fun initListeners() {
        photoShareButton.setOnClickListener {
            startActivity(viewModel.createShareIntent())
        }

        photoDownloadButton.setOnClickListener {

        }
    }

    private fun initObservers() {
        viewModel.loadPhoto(Glide.with(this)).observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                photoImageView.setImageBitmap(it)
            }
        })

        viewModel.favoriteChangedEvent.observe(this, Observer {
            favoriteMenuItemIcon = if (it?.getContentIfNotHandled() == true) {
                R.drawable.ic_favorite_white_24dp
            } else {
                R.drawable.ic_favorite_border_white_24dp
            }

            invalidateOptionsMenu()
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setTransitionAnimationDuration() {
        val duration = 220L

        val changeBounds = ChangeBounds()
        changeBounds.duration = duration

        window.sharedElementEnterTransition = changeBounds
    }

    private fun adjustMargins() {
        val photoAuthorTextLayoutParams = photoAuthorText.layoutParams

        if (photoAuthorTextLayoutParams is ViewGroup.MarginLayoutParams) {
            photoAuthorTextLayoutParams.bottomMargin += Utils.navigationBarHeight(this)
        }
    }

    private fun hideViews() {
        photoToolbar.visibility = View.INVISIBLE
        photoAuthorText.visibility = View.INVISIBLE
        photoShareButton.visibility = View.INVISIBLE
        photoDownloadButton.visibility = View.INVISIBLE
    }
}