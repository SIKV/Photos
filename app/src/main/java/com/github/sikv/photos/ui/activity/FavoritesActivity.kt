package com.github.sikv.photos.ui.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.database.PhotoData
import com.github.sikv.photos.ui.adapter.PhotoDataListAdapter
import com.github.sikv.photos.util.setBackgroundColor
import com.github.sikv.photos.viewmodel.FavoritesViewModel
import kotlinx.android.synthetic.main.activity_favorites.*


class FavoritesActivity : BaseActivity() {

    companion object {
        fun startActivity(activity: Activity) {
            val intent = Intent(activity, FavoritesActivity::class.java)

            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private val viewModel: FavoritesViewModel by lazy {
        ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
    }

    private var photoAdapter: PhotoDataListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_favorites)

        setSupportActionBar(favoritesToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        init()

        observeFavorites()
        observeEvents()
    }

    private fun observeFavorites() {
        viewModel.favoritesLiveData.observe(this, Observer {
            it?.let { photos ->
                photoAdapter?.submitList(photos)
                favoritesEmptyLayout.visibility = if (photos.isEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    private fun observeEvents() {
        viewModel.favoritesDeleteEvent.observe(this, Observer { deleted ->
            if (deleted == true) {
                Snackbar.make(favoritesRootLayout, R.string.deleted, Snackbar.LENGTH_LONG)
                        .setBackgroundColor(R.color.colorPrimaryDark)
                        .setAction(R.string.undo) {
                            viewModel.undoDeleteAll()
                        }
                        .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                viewModel.deleteAllForever()
                            }
                        })
                        .show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemDelete -> {
                viewModel.deleteAll()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun onPhotoClick(photo: PhotoData, view: View) {
        PhotoActivity.startActivity(this, view, photo)
    }

    private fun onPhotoLongClick(photo: PhotoData, view: View) {
    }

    private fun init() {
        photoAdapter = PhotoDataListAdapter(Glide.with(this), ::onPhotoClick, ::onPhotoLongClick)
        favoritesRecycler.adapter = photoAdapter
    }
}