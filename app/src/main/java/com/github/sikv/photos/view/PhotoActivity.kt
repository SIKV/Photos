package com.github.sikv.photos.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.activity_photo.*

class PhotoActivity : BaseActivity() {

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

        init()

        val photo = intent.getParcelableExtra<Photo>(EXTRA_PHOTO)

        Glide.with(this).load(photo.urls.regular).into(photoImageView)
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
    }
}