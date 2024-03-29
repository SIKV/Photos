package com.github.sikv.photos.search

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.domain.PhotoSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val favoritesRepository: FavoritesRepository,
    private val configProvider: ConfigProvider
) : ViewModel() {

    private val mutableSearchQueryState = MutableStateFlow<SearchQuery?>(null)
    val searchQueryState: StateFlow<SearchQuery?> = mutableSearchQueryState

    fun favoriteUpdates(): Flow<FavoritesRepository.Update> {
        return favoritesRepository.favoriteUpdates()
    }

    fun toggleFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    fun requestSearch(text: String) {
        mutableSearchQueryState.update {
            SearchQuery(query = text)
        }
    }

    fun clearSearch() {
        mutableSearchQueryState.update {
            null
        }
    }

    fun searchPhotos(photoSource: PhotoSource, searchQuery: SearchQuery): Flow<PagingData<Photo>>? {
        val queryTrimmed = searchQuery.query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        return Pager(
            config = configProvider.getPagingConfig(),
            pagingSourceFactory = {
                SearchPhotosPagingSource(
                    photosRepository,
                    photoSource,
                    queryTrimmed
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
