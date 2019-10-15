package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.database.FavoritesDatabase
import com.github.sikv.photos.database.PhotoData
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoDataListAdapter
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.SPAN_COUNT_GRID
import com.github.sikv.photos.util.SPAN_COUNT_LIST
import com.github.sikv.photos.util.setBackgroundColor
import com.github.sikv.photos.util.setTextColor
import com.github.sikv.photos.viewmodel.FavoritesViewModel
import com.github.sikv.photos.viewmodel.FavoritesViewModelFactory
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : BaseFragment() {

    companion object {
        private const val KEY_CURRENT_SPAN_COUNT = "key_current_span_count"
    }

    private val viewModel: FavoritesViewModel by lazy {
        val application = requireNotNull(activity).application

        val viewModelFactory = FavoritesViewModelFactory(application,
                FavoritesDatabase.getInstance(application).favoritesDao)

        ViewModelProviders.of(this, viewModelFactory)
                .get(FavoritesViewModel::class.java)
    }

    private var photoAdapter: PhotoDataListAdapter? = null

    private var currentSpanCount: Int = SPAN_COUNT_LIST
        set(value) {
            field = value

            setRecyclerLayoutManager(value)

            setMenuItemVisibility(R.id.itemViewList, field == SPAN_COUNT_GRID)
            setMenuItemVisibility(R.id.itemViewGrid, field == SPAN_COUNT_LIST)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        if (savedInstanceState != null) {
            currentSpanCount = savedInstanceState.getInt(KEY_CURRENT_SPAN_COUNT, SPAN_COUNT_LIST)
        } else {
            currentSpanCount = SPAN_COUNT_LIST
        }

        observeFavorites()
        observeEvents()
    }

    override fun onCreateToolbar(): FragmentToolbar? {
        return FragmentToolbar.Builder()
                .withId(R.id.favoritesToolbar)
                .withMenu(R.menu.menu_favorites)
                .withMenuItems(
                        listOf(
                                R.id.itemViewList,
                                R.id.itemViewGrid,
                                R.id.itemDelete),
                        listOf(
                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        currentSpanCount = SPAN_COUNT_LIST
                                        return true
                                    }
                                },

                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        currentSpanCount = SPAN_COUNT_GRID
                                        return true
                                    }
                                },

                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        viewModel.deleteAll()
                                        return true
                                    }
                                }
                        )
                )
                .build()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_CURRENT_SPAN_COUNT, currentSpanCount)
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
        viewModel.favoritesDeleteEvent.observe(this, Observer { deleteEvent ->
            if (deleteEvent.getContentIfNotHandled() == true) {
                Snackbar.make(favoritesRootLayout, R.string.deleted, Snackbar.LENGTH_LONG)
                        .setTextColor(R.color.colorText)
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

    private fun onPhotoClick(photo: PhotoData, view: View) {
        PhotoActivity.startActivity(activity!!, view, photo)
    }

    private fun onPhotoLongClick(photo: PhotoData, view: View) {
    }

    private fun setRecyclerLayoutManager(spanCount: Int) {
        favoritesRecycler.layoutManager = GridLayoutManager(context, spanCount)
    }

    private fun init() {
        photoAdapter = PhotoDataListAdapter(Glide.with(this), ::onPhotoClick, ::onPhotoLongClick)
        favoritesRecycler.adapter = photoAdapter

        // TODO Default value is not working good. When a photo is removed animation is broken.
        favoritesRecycler.itemAnimator?.removeDuration = 50
    }
}