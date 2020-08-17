package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class PexelsCuratedPhotosDataSource : PositionalDataSource<Photo>() {

    @Inject
    lateinit var photosRepository: PhotosRepository

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    init {
        App.instance.appComponent.inject(this)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        scope.launch {
            try {
                val photos = photosRepository.getLatestPhotos(
                        params.requestedStartPosition, params.requestedLoadSize, PhotoSource.PEXELS)

                callback.onResult(photos, 0)
                updateState(DataSourceState.INITIAL_LOADING_DONE)
            } catch (e: Exception) {
                updateState(DataSourceState.ERROR)
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(DataSourceState.LOADING_NEXT)

        scope.launch {
            try {
                val photos = photosRepository.getLatestPhotos(
                        params.startPosition, params.loadSize, PhotoSource.PEXELS)

                callback.onResult(photos)
                updateState(DataSourceState.NEXT_DONE)
            } catch (e: Exception) {
                updateState(DataSourceState.ERROR)
            }
        }
    }

    private fun updateState(state: DataSourceState) {
        this.state.postValue(state)
    }
}