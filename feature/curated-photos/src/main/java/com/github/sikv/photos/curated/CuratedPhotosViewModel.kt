package com.github.sikv.photos.curated

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.common.PreferencesService
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class CuratedPhotosViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val favoritesRepository: FavoritesRepository,
    private val preferencesService: PreferencesService,
    private val configProvider: ConfigProvider
) : ViewModel() {

    private val mutableListLayoutState = MutableStateFlow(preferencesService.getCuratedListLayout())
    val listLayoutState: StateFlow<ListLayout> = mutableListLayoutState

    fun favoriteUpdates(): Flow<FavoritesRepository.Update> {
        return favoritesRepository.favoriteUpdates()
    }

    fun toggleFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    fun updateListLayout(listLayout: ListLayout) {
        preferencesService.setCuratedListLayout(listLayout)
        mutableListLayoutState.value = listLayout
    }

    fun getCuratedPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = configProvider.getPagingConfig(),
            pagingSourceFactory = {
                CuratedPhotosPagingSource(photosRepository)
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
