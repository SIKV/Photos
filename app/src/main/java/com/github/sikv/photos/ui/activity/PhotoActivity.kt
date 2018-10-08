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
import android.transition.ChangeBounds
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.viewmodel.PhotoViewModel
import com.github.sikv.photos.viewmodel.PhotoViewModelFactory
import kotlinx.android.synthetic.main.activity_photo.*


class PhotoActivity : AppCompatActivity() {

    private lateinit var viewModel: PhotoViewModel

    companion object {

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

        viewModel = ViewModelProviders.of(this, PhotoViewModelFactory(photo))
                .get(PhotoViewModel::class.java)

        init(photo)
        initListeners()

        viewModel.loadPhoto(Glide.with(this)).observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                photoImageView.setImageBitmap(it)
            }
        })
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

    private fun init(photo: Photo) {
        setSupportActionBar(photoToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val authorFullName = photo.user.name
        val source = getString(R.string.unsplash)

        photoAuthorText.text = String.format(getString(R.string.photo_by_s_on_s), authorFullName, source)

        Utils.makeUnderlineBold(photoAuthorText, arrayOf(authorFullName, source))
    }

    private fun initListeners() {
        photoShareButton.setOnClickListener {
            startActivity(viewModel.createShareIntent())
        }

        photoDownloadButton.setOnClickListener {

        }
    }

    private fun hideViews() {
        photoToolbar.visibility = View.INVISIBLE
        photoAuthorText.visibility = View.INVISIBLE
        photoShareButton.visibility = View.INVISIBLE
        photoDownloadButton.visibility = View.INVISIBLE
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setTransitionAnimationDuration() {
        val duration = 220L

        val changeBounds = ChangeBounds()
        changeBounds.duration = duration

        window.sharedElementEnterTransition = changeBounds
    }
}