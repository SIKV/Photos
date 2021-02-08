package com.github.sikv.photos.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.favoriteAnimation
import com.github.sikv.photos.util.makeClickable
import com.github.sikv.photos.util.makeUnderlineBold
import com.github.sikv.photos.viewmodel.PhotoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_photo.*

@AndroidEntryPoint
class PhotoActivity : BaseActivity() {

    companion object {
        fun startActivity(activity: Activity, transitionView: View, photo: Photo) {
            val intent = Intent(activity, PhotoActivity::class.java).apply {
                putExtra(Photo.KEY, photo)
            }

            activity.startActivity(intent)
        }
    }

    private val viewModel: PhotoViewModel by viewModels()

    private var favoriteMenuItemIcon: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setListeners()
        setOnApplyWindowInsetsListeners()

        observe()
        observeGlobalMessageEvent()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_photo, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        favoriteMenuItemIcon?.let {
            menu?.findItem(R.id.itemFavorite)?.setIcon(it)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemFavorite -> {
                viewModel.invertFavorite()
                findViewById<View>(R.id.itemFavorite).favoriteAnimation()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showPhotoInfo(photo: Photo) {
        val authorName = photo.getPhotoPhotographerName()
        val source = photo.getPhotoSource().title

        authorText.text = String.format(getString(R.string.photo_by_s_on_s), authorName, source)

        authorText.makeUnderlineBold(arrayOf(authorName, source))

        authorText.makeClickable(arrayOf(authorName, source),
                arrayOf(
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                viewModel.openAuthorUrl()
                            }
                        },
                        object : ClickableSpan() {
                            override fun onClick(view: View) {
                                viewModel.openPhotoSource()
                            }
                        }
                ))
    }

    private fun updateFavoriteMenuItemIcon(favorite: Boolean) {
        favoriteMenuItemIcon = if (favorite) {
            R.drawable.ic_favorite_red_24dp
        } else {
            R.drawable.ic_favorite_border_white_24dp
        }

        invalidateOptionsMenu()
    }

    private fun observe() {
        viewModel.showPhotoInfoEvent.observe(this, { photo ->
            photo?.let(::showPhotoInfo)
        })

        viewModel.showPhotoEvent.observe(this, { photo ->
            photoImageView.setImageBitmap(photo)
        })

        viewModel.favoriteInitEvent.observe(this, {
            it?.getContentIfNotHandled()?.let { favorite ->
                updateFavoriteMenuItemIcon(favorite)
            }
        })

        viewModel.favoriteChangedLiveData.observe(this, {
            updateFavoriteMenuItemIcon(it)
        })
    }

    private fun setListeners() {
        setWallpaperButton.setOnClickListener {
            viewModel.setWallpaper(supportFragmentManager)
        }

        downloadButton.setOnClickListener {
            requestWriteExternalStoragePermission {
                viewModel.downloadPhotoAndSave()
            }
        }

        shareButton.setOnClickListener {
            startActivity(viewModel.createShareIntent())
        }
    }

    private fun setOnApplyWindowInsetsListeners() {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            view.updatePadding(
                    top = insets.systemWindowInsetTop,
                    left = insets.systemWindowInsetLeft,
                    right = insets.systemWindowInsetRight)

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(contentLayout) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun observeGlobalMessageEvent() {
        App.instance.globalMessageEvent.observe(this, { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(rootLayout, message, Snackbar.LENGTH_SHORT)
                        .show()
            }
        })
    }
}