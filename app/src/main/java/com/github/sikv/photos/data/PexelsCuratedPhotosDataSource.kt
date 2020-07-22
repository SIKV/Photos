package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.api.PexelsClient
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PexelsCuratedPhotosDataSource(private val apiClient: PexelsClient) : PositionalDataSource<Photo>() {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        scope.launch {
            try {
                val result = apiClient.getCuratedPhotos(params.requestedStartPosition, params.requestedLoadSize)

                callback.onResult(result.photos, 0)
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
                val result = apiClient.getCuratedPhotos(params.startPosition, params.loadSize)

                callback.onResult(result.photos)
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