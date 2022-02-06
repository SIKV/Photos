package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.sikv.photos.R
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.databinding.FragmentFavoritesBinding
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.model.ListLayout
import com.github.sikv.photos.model.PhotoItemLayoutType
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoListAdapter
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.scrollToTop
import com.github.sikv.photos.util.setItemLayoutType
import com.github.sikv.photos.util.setupToolbar
import com.github.sikv.photos.viewmodel.FavoritesViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : BaseFragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    @Inject
    lateinit var downloadService: DownloadService

    @Inject
    lateinit var photoLoader: PhotoLoader

    private val viewModel: FavoritesViewModel by viewModels()

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(
            fragment = this,
            downloadService = downloadService,
            photoLoader = photoLoader,
            onToggleFavorite = viewModel::toggleFavorite,
            onShowMessage = ::showMessage
        )
    }

    private lateinit var photoAdapter: PhotoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoAdapter = PhotoListAdapter(
            photoLoader = photoLoader,
            favoritesRepository = favoritesRepository,
            lifecycleScope = lifecycleScope,
            listener = photoActionDispatcher
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(R.string.favorites)

        binding.photosRecycler.adapter = photoAdapter

        // Default value is not working good. When a photo is removed animation is broken.
        binding.photosRecycler.itemAnimator?.removeDuration = 0

        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onCreateToolbar(): FragmentToolbar {
        return FragmentToolbar.Builder()
            .withId(R.id.toolbar)
            .withMenu(R.menu.menu_favorites)
            .withMenuItems(
                listOf(
                    R.id.itemViewList,
                    R.id.itemViewGrid,
                    R.id.itemSortBy,
                    R.id.itemRemoveAll
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

    override fun onScrollToTop() {
        binding.photosRecycler.scrollToTop()
    }

    private fun observe() {
        viewModel.favoritesLiveData.observe(viewLifecycleOwner, {
            it?.let { photos ->
                photoAdapter.submitList(photos)
                binding.noResultsView.isVisible = photos.isEmpty()
            }
        })

        viewModel.removeAllResultEvent.observe(viewLifecycleOwner, { event ->
            if (event.getContentIfNotHandled() == true) {
                Snackbar.make(binding.root, R.string.removed, Snackbar.LENGTH_LONG)
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

        viewModel.listLayoutChanged.observe(viewLifecycleOwner, { listLayout ->
            updateListLayout(listLayout)
        })
    }

    private fun updateListLayout(listLayout: ListLayout) {
        val itemLayoutType = PhotoItemLayoutType.findBySpanCount(listLayout.spanCount)

        photoAdapter.setItemLayoutType(itemLayoutType)
        binding.photosRecycler.setItemLayoutType(itemLayoutType)

        setMenuItemVisibility(R.id.itemViewList, listLayout == ListLayout.GRID)
        setMenuItemVisibility(R.id.itemViewGrid, listLayout == ListLayout.LIST)
    }
}
