package com.github.sikv.photos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.data.source.SearchPhotosPagingSource
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.PhotoSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val favoritesRepository: FavoritesRepository,
    private val configProvider: ConfigProvider
) : ViewModel() {

    private val mutableSearchQueryFlow = MutableSharedFlow<String>(replay = 1)
    val searchQueryFlow: Flow<String> = mutableSearchQueryFlow

    fun favoriteUpdates(): Flow<FavoritesRepository.Update> {
        return favoritesRepository.favoriteUpdates()
    }

    fun toggleFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    fun requestSearch(text: String) {
        viewModelScope.launch {
            mutableSearchQueryFlow.emit(text)
        }
    }

    fun searchPhotos(photoSource: PhotoSource, query: String): Flow<PagingData<Photo>>? {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        return Pager(
            config = configProvider.getPagingConfig(),
            pagingSourceFactory = {
                SearchPhotosPagingSource(photosRepository, photoSource, queryTrimmed)
            }
        ).flow
    }
}
