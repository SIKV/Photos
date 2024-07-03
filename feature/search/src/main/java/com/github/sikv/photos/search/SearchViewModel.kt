package com.github.sikv.photos.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.repository.FavoritesRepository2
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import com.github.sikv.photos.navigation.args.SearchFragmentArguments
import com.github.sikv.photos.navigation.args.fragmentArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photosRepository: PhotosRepository,
    private val favoritesRepository: FavoritesRepository2,
    private val configProvider: ConfigProvider
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = mutableUiState

    init {
        val args = savedStateHandle.fragmentArguments<SearchFragmentArguments>()

        mutableUiState.update { state ->
            state.copy(
                query = args.query,
                photoSources = configProvider.getSearchSources()
            )
        }
    }

    fun isFavorite(photo: Photo): Flow<Boolean> {
        return favoritesRepository.getFavorites()
            .map { photos ->
                photos.contains(photo)
            }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            favoritesRepository.invertFavorite(photo)
        }
    }

    fun switchListLayout() {
        val listLayout = uiState.value.listLayout

        val switchedListLayout = when (listLayout) {
            ListLayout.LIST -> ListLayout.GRID
            ListLayout.GRID -> ListLayout.LIST
        }

        mutableUiState.update { state ->
            state.copy(listLayout = switchedListLayout)
        }
    }

    fun onSearchQueryChange(text: String) {
        mutableUiState.update { state ->
            state.copy(query = text)
        }
    }

    fun performSearch() {
        val query = uiState.value.query?.trim().orEmpty()

        if (query.isEmpty()) {
            return
        }

        val mutableSearchFlows = uiState.value.photos.toMutableMap()

        uiState.value.photoSources.forEach { photoSource ->
            mutableSearchFlows[photoSource] = searchPhotos(query, photoSource)
        }

        mutableUiState.update { state ->
            state.copy(photos = mutableSearchFlows)
        }
    }

    private fun searchPhotos(query: String, photoSource: PhotoSource): Flow<PagingData<Photo>> {
        return Pager(
            config = configProvider.getPagingConfig(),
            pagingSourceFactory = {
                SearchPhotosPagingSource(
                    photosRepository,
                    photoSource,
                    query
                )
            }
        ).flow
    }
}

private fun ConfigProvider.getPagingConfig(): PagingConfig {
    val page = getPageConfig()

    return PagingConfig(
        initialLoadSize = page.initialLoadSize,
        pageSize = page.pageSize,
        enablePlaceholders = page.enablePlaceholders
    )
}
