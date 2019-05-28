package com.github.sikv.photos.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.*
import com.bumptech.glide.Glide
import com.github.sikv.photos.R
import com.github.sikv.photos.database.PhotoData
import com.github.sikv.photos.ui.activity.PhotoActivity
import com.github.sikv.photos.ui.adapter.PhotoDataListAdapter
import com.github.sikv.photos.util.setBackgroundColor
import com.github.sikv.photos.viewmodel.FavoritesViewModel
import kotlinx.android.synthetic.main.fragment_favorites.*


class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by lazy {
        ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
    }

    private var photoAdapter: PhotoDataListAdapter? = null

    private var currentSpanCount: Int = 2
        set(value) {
            field = value
            setFavoritesRecyclerLayoutManager(value)
        }

    private var viewListOptionVisible = true
    private var viewGridOptionVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(favoritesToolbar)

            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        init()

        observeFavorites()
        observeEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_favorites, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        menu?.findItem(R.id.itemViewList)?.isVisible = viewListOptionVisible
        menu?.findItem(R.id.itemViewGrid)?.isVisible = viewGridOptionVisible
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemViewList -> {
                currentSpanCount = 1

                viewListOptionVisible = false
                viewGridOptionVisible = true

                (activity as? AppCompatActivity)?.apply {
                    invalidateOptionsMenu()
                }
            }

            R.id.itemViewGrid -> {
                currentSpanCount = 2

                viewListOptionVisible = true
                viewGridOptionVisible = false

                (activity as? AppCompatActivity)?.apply {
                    invalidateOptionsMenu()
                }
            }

            R.id.itemDelete -> {
                viewModel.deleteAll()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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

    private fun onPhotoClick(photo: PhotoData, view: View) {
        PhotoActivity.startActivity(activity!!, view, photo)
    }

    private fun onPhotoLongClick(photo: PhotoData, view: View) {
    }

    private fun setFavoritesRecyclerLayoutManager(spanCount: Int) {
        favoritesRecycler.layoutManager = GridLayoutManager(context, spanCount)
    }

    private fun init() {
        photoAdapter = PhotoDataListAdapter(Glide.with(this), ::onPhotoClick, ::onPhotoLongClick)
        favoritesRecycler.adapter = photoAdapter

        setFavoritesRecyclerLayoutManager(currentSpanCount)
    }
}