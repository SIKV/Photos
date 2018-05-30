package com.github.sikv.photos.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.photo_activity.*

class PhotoActivity : BaseActivity() {

    companion object {

        private const val EXTRA_PHOTO = "photo"

        fun startActivity(context: Context, photo: Photo) {
            val intent = Intent(context, PhotoActivity::class.java)
            intent.putExtra(EXTRA_PHOTO, photo)

            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.photo_activity)

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