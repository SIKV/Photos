package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.R
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.database.entity.CuratedPhotoEntity
import com.github.sikv.photos.enumeration.ListLayout
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoPagingAdapter
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.*
import com.github.sikv.photos.viewmodel.PhotosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlinx.android.synthetic.main.layout_loading_error.*
import kotlinx.android.synthetic.main.layout_loading_list.*
import javax.inject.Inject

@AndroidEntryPoint
class PhotosFragment : BaseFragment() {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    private val viewModel: PhotosViewModel by viewModels()

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(this, glide) { photo ->
            viewModel.invertFavorite(photo)
        }
    }

    private lateinit var photoAdapter: PhotoPagingAdapter<CuratedPhotoEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoAdapter = PhotoPagingAdapter(glide, favoritesRepository, photoActionDispatcher)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(R.string.app_name)

        photosRecycler.adapter = photoAdapter
        photosRecycler.disableChangeAnimations()

        tryAgainButton.setOnClickListener {
            photoAdapter.retry()
        }

        initAdapter()
        observe()
    }

    override fun onCreateToolbar(): FragmentToolbar {
        return FragmentToolbar.Builder()
                .withId(R.id.toolbar)
                .withMenu(R.menu.menu_photos)
                .withMenuItems(
                        listOf(
                                R.id.itemViewList,
                                R.id.itemViewGrid
                        ),
                        listOf(
                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        viewModel.updateListLayout(ListLayout.LIST)
                                        return true
                                    }
                                },

                                object : MenuItem.OnMenuItemClickListener {
                                    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
                                        viewModel.updateListLayout(ListLayout.GRID)
                                        return true
                                    }
                                }
                        )
                )
                .build()
    }

    override fun onScrollToTop() {
        photosRecycler.scrollToTop()
    }

    private fun observe() {
        viewModel.getCuratedPhotos().observe(viewLifecycleOwner, {
            photoAdapter.submitData(lifecycle, it)
        })

        viewModel.favoriteChangedEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { photo ->
                photoAdapter.notifyPhotoChanged(photo)
            }
        })

        viewModel.favoritesChangedEvent.observe(viewLifecycleOwner, {
            if (it.canHandle()) {
                photoAdapter.notifyDataSetChanged()
            }
        })

        viewModel.listLayoutChanged.observe(viewLifecycleOwner, { listLayout ->
            updateListLayout(listLayout)
        })
    }

    private fun updateListLayout(listLayout: ListLayout) {
        val itemLayoutType = PhotoItemLayoutType.findBySpanCount(listLayout.spanCount)

        photoAdapter.setItemLayoutType(itemLayoutType)
        photosRecycler.setItemLayoutType(itemLayoutType)

        setMenuItemVisibility(R.id.itemViewList, listLayout == ListLayout.GRID)
        setMenuItemVisibility(R.id.itemViewGrid, listLayout == ListLayout.LIST)
    }

    private fun initAdapter() {
        photoAdapter.addLoadStateListener { loadState ->
            when (loadState.mediator?.refresh) {
                is LoadState.NotLoading -> {
                    photosRecycler.setVisibilityAnimated(View.VISIBLE)
                    loadingListLayout.setVisibilityAnimated(View.GONE)
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)
                }

                is LoadState.Loading -> {
                    loadingErrorLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                }

                is LoadState.Error -> {
                    photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingListLayout.setVisibilityAnimated(View.GONE, duration = 0)
                    loadingErrorLayout.setVisibilityAnimated(View.VISIBLE, duration = 0)
                }
            }
        }
    }
}