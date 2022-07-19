package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.liveData
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.data.source.CuratedPhotosPagingSource
import com.github.sikv.photos.model.ListLayout
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.service.PreferencesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val favoritesRepository: FavoritesRepository,
    private val preferencesService: PreferencesService,
    private val configProvider: ConfigProvider
) : ViewModel() {

    private val _listLayoutState = MutableStateFlow(preferencesService.getCuratedListLayout())
    val listLayoutState: StateFlow<ListLayout> = _listLayoutState

    fun favoriteUpdates(): Flow<FavoritesRepository.Update> {
        return favoritesRepository.favoriteUpdates()
    }

    fun toggleFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    fun updateListLayout(listLayout: ListLayout) {
        preferencesService.setCuratedListLayout(listLayout)
        _listLayoutState.value = listLayout
    }

    fun getCuratedPhotos(): LiveData<PagingData<Photo>> {
        return Pager(
            config = configProvider.getPagingConfig(),
            pagingSourceFactory = {
                CuratedPhotosPagingSource(photosRepository)
            }
        ).liveData
    }
}
