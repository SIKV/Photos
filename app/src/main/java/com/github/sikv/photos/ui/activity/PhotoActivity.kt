package com.github.sikv.photos.ui.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Utils
import com.github.sikv.photos.viewmodel.PhotoViewModel
import kotlinx.android.synthetic.main.activity_photo.*


class PhotoActivity : AppCompatActivity() {

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


    private val viewModel: PhotoViewModel by lazy {
        ViewModelProviders.of(this).get(PhotoViewModel::class.java)
    }

    private var photo: Photo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo)

        photo = intent.getParcelableExtra(EXTRA_PHOTO)

        init()

        photo?.let { photo ->
            viewModel.loadPhoto(Glide.with(this), photo).observe(this, Observer {
                it?.getContentIfNotHandled()?.let {
                    photoImageView.setImageBitmap(it)
                }
            })
        }
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

    private fun init() {
        setSupportActionBar(photoToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val authorFullName = photo?.user?.name ?: "?"
        val source = getString(R.string.unsplash)

        photoAuthorText.text = String.format(getString(R.string.photo_by_s_on_s), authorFullName, source)

        Utils.makeUnderlineBold(photoAuthorText, arrayOf(authorFullName, source))
    }

    private fun hideViews() {
        photoToolbar.visibility = View.INVISIBLE
        photoAuthorText.visibility = View.INVISIBLE
        photoShareButton.visibility = View.INVISIBLE
        photoDownloadButton.visibility = View.INVISIBLE
    }
}