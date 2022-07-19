package com.github.sikv.photos.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.databinding.FragmentSingleSearchBinding
import com.github.sikv.photos.manager.PhotoLoader
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.PhotoSource
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.FragmentArguments
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoPagingAdapter
import com.github.sikv.photos.ui.fragmentArguments
import com.github.sikv.photos.util.disableChangeAnimations
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class SingleSearchFragmentArguments(
    val photoSource: PhotoSource
) : FragmentArguments

@AndroidEntryPoint
class SingleSearchFragment : BaseFragment() {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    @Inject
    lateinit var downloadService: DownloadService

    @Inject
    lateinit var photoLoader: PhotoLoader

    private val viewModel: SearchViewModel by activityViewModels()
    private val args by fragmentArguments<SingleSearchFragmentArguments>()

    private var _binding: FragmentSingleSearchBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentSingleSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photosRecycler.adapter = photoAdapter
        binding.photosRecycler.disableChangeAnimations()

        binding.loadingView.isVisible = false
        binding.noResultsView.isVisible = false
        binding.loadingErrorView.isVisible = false

        binding.loadingErrorView.setTryAgainClickListener {
            photoAdapter.retry()
        }

        addLoadStateListener()

        observeSearchQuery()
        collectFavoriteUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun observeSearchQuery() {
        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            viewModel.searchPhotos(args.photoSource, query)?.observe(viewLifecycleOwner) { data ->
                photoAdapter.submitData(lifecycle, data)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collectFavoriteUpdates() {
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
            when (loadState.source.refresh) {
                is LoadState.NotLoading -> {
                    binding.photosRecycler.setVisibilityAnimated(View.VISIBLE)
                    binding.noResultsView.isVisible = photoAdapter.itemCount == 0
                }
                is LoadState.Loading -> {
                    binding.noResultsView.isVisible = false
                    binding.photosRecycler.setVisibilityAnimated(View.GONE)
                }
                is LoadState.Error -> {
                    binding.noResultsView.isVisible = false
                    binding.photosRecycler.setVisibilityAnimated(View.GONE)
                }
            }

            binding.loadingView.updateLoadState(loadState)
            binding.loadingErrorView.updateLoadState(loadState)
        }
    }
}
