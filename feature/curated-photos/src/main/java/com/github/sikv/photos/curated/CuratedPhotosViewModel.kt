package com.github.sikv.photos.curated

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.sikv.photo.usecase.DownloadPhotoUseCase
import com.github.sikv.photo.usecase.PhotoActionsUseCase
import com.github.sikv.photos.common.PreferencesService
import com.github.sikv.photos.common.ui.openUrl
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.createShareIntent
import com.github.sikv.photos.data.repository.FavoritesRepository2
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CuratedPhotosViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val favoritesRepository: FavoritesRepository2,
    private val preferencesService: PreferencesService,
    private val configProvider: ConfigProvider,
    private val photoActionsUseCase: PhotoActionsUseCase,
    private val downloadPhotoUseCase: DownloadPhotoUseCase
) : ViewModel() {

    private val mutableListLayoutState = MutableStateFlow(preferencesService.getCuratedListLayout())
    val listLayoutState: StateFlow<ListLayout> = mutableListLayoutState

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

    // TODO: This is temporary solution!
    // Will be refactored after migration to Compose Navigation is finished.
    fun onPhotoAttributionClick(activity: AppCompatActivity, photo: Photo) {
        photo.getPhotoPhotographerUrl()?.let { photographerUrl ->
            activity.openUrl(photographerUrl)
        } ?: run {
            activity.openUrl(photo.getPhotoShareUrl())
        }
    }

    // TODO: This is temporary solution!
    // Will be refactored after migration to Compose Navigation is finished.
    fun openActions(activity: AppCompatActivity, photo: Photo) {
        photoActionsUseCase.openActions(activity.supportFragmentManager, photo) { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    // TODO: This is temporary solution!
    // Will be refactored after migration to Compose Navigation is finished.
    fun downloadPhoto(activity: AppCompatActivity, photo: Photo) {
        downloadPhotoUseCase.download(activity, photo) { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    // TODO: This is temporary solution!
    // Will be refactored after migration to Compose Navigation is finished.
    fun sharePhoto(activity: AppCompatActivity, photo: Photo) {
        activity.startActivity(photo.createShareIntent())
    }

    fun switchListLayout() {
        when (listLayoutState.value) {
            ListLayout.LIST -> {
                preferencesService.setCuratedListLayout(ListLayout.GRID)
                mutableListLayoutState.value = ListLayout.GRID
            }
            ListLayout.GRID -> {
                preferencesService.setCuratedListLayout(ListLayout.LIST)
                mutableListLayoutState.value = ListLayout.LIST
            }
        }
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
