package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.databinding.FragmentSingleSearchBinding
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.PhotoActionDispatcher
import com.github.sikv.photos.ui.adapter.PhotoPagingAdapter
import com.github.sikv.photos.util.disableChangeAnimations
import com.github.sikv.photos.util.setVisibilityAnimated
import com.github.sikv.photos.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SingleSearchFragment : BaseFragment() {

    companion object {
        private const val KEY_PHOTO_SOURCE_ID = "photoSourceId"

        fun newInstance(photoSource: PhotoSource): SingleSearchFragment {
            val args = Bundle()
            args.putInt(KEY_PHOTO_SOURCE_ID, photoSource.id)

            val fragment = SingleSearchFragment()
            fragment.arguments = args

            return fragment
        }
    }

    private var _binding: FragmentSingleSearchBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    @Inject
    lateinit var downloadService: DownloadService

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: SearchViewModel by viewModels()

    private val photoActionDispatcher by lazy {
        PhotoActionDispatcher(
            fragment = this,
            downloadService = downloadService,
            glide = glide,
            onToggleFavorite = viewModel::toggleFavorite,
            onShowMessage = ::showMessage
        )
    }

    private var photoSource: PhotoSource? = null

    private lateinit var photoAdapter: PhotoPagingAdapter<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt(KEY_PHOTO_SOURCE_ID)?.let { photoSourceId ->
            photoSource = PhotoSource.findById(photoSourceId)
        }

        photoAdapter = PhotoPagingAdapter(
            glide = glide,
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
        _binding = FragmentSingleSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photosRecycler.adapter = photoAdapter
        binding.photosRecycler.disableChangeAnimations()

        binding.loadingErrorLayout.tryAgainButton.setOnClickListener {
            photoAdapter.retry()
        }

        initAdapter()
        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun searchPhotos(text: String) {
        photoSource?.let { photoSource ->
            viewModel.searchPhotos(photoSource, text)?.observe(viewLifecycleOwner, Observer {
                photoAdapter.submitData(lifecycle, it)
            })
        }
    }

    private fun observe() {
        viewModel.favoriteChangedEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { photo ->
                photoAdapter.notifyPhotoChanged(photo)
            }
        })

        viewModel.favoritesChangedEvent.observe(viewLifecycleOwner, Observer {
            if (it.canHandle()) {
                photoAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initAdapter() {
        photoAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.NotLoading -> {
                    binding.photosRecycler.setVisibilityAnimated(View.VISIBLE)
                    binding.loadingListLayout.root.setVisibilityAnimated(View.GONE)
                    binding.loadingErrorLayout.root.setVisibilityAnimated(View.GONE, duration = 0)

                    if (photoAdapter.itemCount == 0) {
                        binding.noResultsFoundLayout.root.setVisibilityAnimated(
                            View.VISIBLE,
                            duration = 0
                        )
                    } else {
                        binding.noResultsFoundLayout.root.setVisibilityAnimated(
                            View.GONE,
                            duration = 0
                        )
                    }
                }

                is LoadState.Loading -> {
                    binding.loadingErrorLayout.root.setVisibilityAnimated(View.GONE, duration = 0)
                    binding.photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    binding.loadingListLayout.root.setVisibilityAnimated(View.VISIBLE, duration = 0)
                    binding.noResultsFoundLayout.root.setVisibilityAnimated(View.GONE, duration = 0)
                }

                is LoadState.Error -> {
                    binding.photosRecycler.setVisibilityAnimated(View.GONE, duration = 0)
                    binding.loadingListLayout.root.setVisibilityAnimated(View.GONE, duration = 0)
                    binding.loadingErrorLayout.root.setVisibilityAnimated(
                        View.VISIBLE,
                        duration = 0
                    )
                    binding.noResultsFoundLayout.root.setVisibilityAnimated(View.GONE, duration = 0)
                }
            }
        }
    }
}
