package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.sikv.photos.R
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoListAdapter
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.*
import com.github.sikv.photos.viewmodel.FavoritesViewModel
import com.github.sikv.photos.viewmodel.FavoritesViewModelFactory
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : BaseFragment() {

    companion object {
        private const val DEFAULT_SPAN_COUNT = SPAN_COUNT_GRID

        private const val KEY_CURRENT_SPAN_COUNT = "key_current_span_count"
    }

    private val viewModel: FavoritesViewModel by lazy {
        val application = requireNotNull(activity).application

        val viewModelFactory = FavoritesViewModelFactory(application)

        ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)
    }

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(this) { photo ->
            viewModel.invertFavorite(photo)
        }
    }

    private var photoAdapter = PhotoListAdapter(photoActionDispatcher)

    private var currentSpanCount: Int = DEFAULT_SPAN_COUNT
        set(value) {
            field = value

            val itemLayoutType = PhotoItemLayoutType.findBySpanCount(field)

            photoAdapter.setItemLayoutType(itemLayoutType)
            photosRecycler.setItemLayoutType(itemLayoutType)

            setMenuItemVisibility(R.id.itemViewList, field == SPAN_COUNT_GRID)
            setMenuItemVisibility(R.id.itemViewGrid, field == SPAN_COUNT_LIST)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(R.string.favorites)

        photosRecycler.adapter = photoAdapter

        // Default value is not working good. When a photo is removed animation is broken.
        photosRecycler.itemAnimator?.removeDuration = 0

        if (savedInstanceState != null) {
            currentSpanCount = savedInstanceState.getInt(KEY_CURRENT_SPAN_COUNT, DEFAULT_SPAN_COUNT)
        } else {
            currentSpanCount = DEFAULT_SPAN_COUNT
        }

        observe()
    }

    override fun onCreateToolbar(): FragmentToolbar? {
        return FragmentToolbar.Builder()
                .withId(R.id.toolbar)
                .withMenu(R.menu.menu_favorites)
                .withMenuItems(
                        listOf(
                                R.id.itemViewList,
                                R.id.itemViewGrid,
                                R.id.itemSortBy,
                                R.id.itemRemoveAll),
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
                                        viewModel.createSortByDialog().show(childFragmentManager)
                                        return true
                                    }
                                },

                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        viewModel.markAllAsRemoved()
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

    override fun onScrollToTop() {
        photosRecycler.scrollToTop()
    }

    private fun observe() {
        viewModel.favoritesLiveData.observe(viewLifecycleOwner, Observer {
            it?.let { photos ->
                photoAdapter.submitList(photos)
                listEmptyLayout.visibility = if (photos.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        viewModel.removeAllResultEvent.observe(viewLifecycleOwner, Observer { event ->
            if (event.getContentIfNotHandled() == true) {
                Snackbar.make(rootLayout, R.string.removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo) {
                            viewModel.unmarkAllAsRemoved()
                        }
                        .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                viewModel.removeAllIfNotUndone()
                            }
                        })
                        .show()
            }
        })
    }
}