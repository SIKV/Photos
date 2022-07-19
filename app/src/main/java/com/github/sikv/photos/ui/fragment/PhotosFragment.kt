package com.github.sikv.photos.ui.fragment

import android.annotation.SuppressLint
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
import androidx.paging.LoadState
import com.github.sikv.photos.R
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.databinding.FragmentPhotosBinding
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.model.ListLayout
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.PhotoItemLayoutType
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoPagingAdapter
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.util.*
import com.github.sikv.photos.viewmodel.PhotosViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PhotosFragment : BaseFragment() {

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    @Inject
    lateinit var downloadService: DownloadService

    @Inject
    lateinit var photoLoader: PhotoLoader

    private val viewModel: PhotosViewModel by viewModels()

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(
            fragment = this,
            downloadService = downloadService,
            photoLoader = photoLoader,
            onToggleFavorite = viewModel::toggleFavorite,
            onShowMessage = ::showMessage
        )
    }

    private lateinit var photoAdapter: PhotoPagingAdapter<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoAdapter = PhotoPagingAdapter(
            photoLoader = photoLoader,
            favoritesRepository = favoritesRepository,
            lifecycleScope = lifecycleScope,
            listener = photoActionDispatcher
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(R.string.photos)

        binding.photosRecycler.adapter = photoAdapter
        binding.photosRecycler.disableChangeAnimations()

        binding.loadingView.isVisible = false
        binding.loadingErrorView.isVisible = false

        binding.loadingErrorView.setTryAgainClickListener {
            photoAdapter.retry()
        }

        addLoadStateListener()
        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
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
        binding.photosRecycler.scrollToTop()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        viewModel.getCuratedPhotos().observe(viewLifecycleOwner) {
            photoAdapter.submitData(lifecycle, it)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listLayoutState.collect(::updateListLayout)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteUpdates().collect { update ->
                    when (update) {
                        is FavoritesRepository.UpdatePhoto -> {
                            photoAdapter.notifyPhotoChanged(update.photo)
                        }
                        is FavoritesRepository.UpdateAll -> {
                            photoAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun addLoadStateListener() {
        photoAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.NotLoading -> {
                    binding.photosRecycler.setVisibilityAnimated(View.VISIBLE)
                }
                is LoadState.Loading -> {
                    binding.photosRecycler.setVisibilityAnimated(View.GONE)
                }
                is LoadState.Error -> {
                    binding.photosRecycler.setVisibilityAnimated(View.GONE)
                }
            }

            binding.loadingView.updateLoadState(loadState)
            binding.loadingErrorView.updateLoadState(loadState)
        }
    }

    private fun updateListLayout(listLayout: ListLayout) {
        val itemLayoutType = PhotoItemLayoutType.findBySpanCount(listLayout.spanCount)

        photoAdapter.setItemLayoutType(itemLayoutType)
        binding.photosRecycler.setItemLayoutType(itemLayoutType)

        setMenuItemVisibility(R.id.itemViewList, listLayout == ListLayout.GRID)
        setMenuItemVisibility(R.id.itemViewGrid, listLayout == ListLayout.LIST)
    }
}
