package com.github.sikv.photos.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.sikv.photo.list.ui.PhotoActionDispatcher
import com.github.sikv.photo.list.ui.adapter.PhotoListAdapter
import com.github.sikv.photo.list.ui.setItemLayoutType
import com.github.sikv.photos.common.DownloadService
import com.github.sikv.photos.common.PhotoLoader
import com.github.sikv.photos.common.ui.BaseFragment
import com.github.sikv.photos.common.ui.scrollToTop
import com.github.sikv.photos.common.ui.setupToolbar
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.favorites.databinding.FragmentFavoritesBinding
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.github.sikv.photos.navigation.route.SetWallpaperRoute
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : BaseFragment() {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    @Inject
    lateinit var downloadService: DownloadService

    @Inject
    lateinit var photoLoader: PhotoLoader

    @Inject
    lateinit var photoDetailsRoute: PhotoDetailsRoute

    @Inject
    lateinit var setWallpaperRoute: SetWallpaperRoute

    private val viewModel: FavoritesViewModel by viewModels()

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(
            fragment = this,
            downloadService = downloadService,
            photoLoader = photoLoader,
            photoDetailsRoute = photoDetailsRoute,
            setWallpaperRoute = setWallpaperRoute,
            onToggleFavorite = viewModel::toggleFavorite,
            onShowMessage = ::showMessage
        )
    }

    private lateinit var photoAdapter: PhotoListAdapter

    private var removedSnackbar: Snackbar? = null

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

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

        collectUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onCreateToolbar(): com.github.sikv.photos.common.ui.toolbar.FragmentToolbar {
        return com.github.sikv.photos.common.ui.toolbar.FragmentToolbar.Builder()
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
                        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                            viewModel.updateListLayout(ListLayout.LIST)
                            return true
                        }
                    },

                    object : MenuItem.OnMenuItemClickListener {
                        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                            viewModel.updateListLayout(ListLayout.GRID)
                            return true
                        }
                    },

                    object : MenuItem.OnMenuItemClickListener {
                        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                            viewModel.createSortByDialog().show(childFragmentManager)
                            return true
                        }
                    },

                    object : MenuItem.OnMenuItemClickListener {
                        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
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

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    updateUiState(uiState)
                }
            }
        }
    }

    private fun updateUiState(uiState: FavoritesUiState) {
        when (uiState) {
            is FavoritesUiState.Data -> {
                photoAdapter.submitList(uiState.photos)
                binding.noResultsView.isVisible = uiState.photos.isEmpty()

                updateListLayout(uiState.listLayout)

                if (uiState.shouldShowRemovedNotification) {
                    showFavoritesRemovedSnackbar()
                }
            }
        }
    }

    private fun showFavoritesRemovedSnackbar() {
        if (removedSnackbar?.isShown == true) {
            return
        }
        removedSnackbar = Snackbar.make(binding.root, R.string.removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                viewModel.unmarkAllAsRemoved()
            }
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    viewModel.removeAllIfNotUndone()
                }
            })

        removedSnackbar?.show()
    }

    private fun updateListLayout(listLayout: ListLayout) {
        val itemLayoutType = com.github.sikv.photo.list.ui.PhotoItemLayoutType.findBySpanCount(listLayout.spanCount)

        photoAdapter.setItemLayoutType(itemLayoutType)
        binding.photosRecycler.setItemLayoutType(itemLayoutType)

        setMenuItemVisibility(R.id.itemViewList, listLayout == ListLayout.GRID)
        setMenuItemVisibility(R.id.itemViewGrid, listLayout == ListLayout.LIST)
    }
}
