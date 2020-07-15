package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.api.PexelsClient
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.subscribeAsync

class PexelsCuratedPhotosDataSource(private val apiClient: PexelsClient) : PositionalDataSource<Photo>() {

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        apiClient.getCuratedPhotos(params.requestedStartPosition, params.requestedLoadSize)
                .subscribeAsync({
                    callback.onResult(it.photos, 0)
                    updateState(DataSourceState.INITIAL_LOADING_DONE)
                }, {
                    updateState(DataSourceState.ERROR)
                })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(DataSourceState.LOADING_NEXT)

        apiClient.getCuratedPhotos(params.startPosition, params.loadSize)
                .subscribeAsync({
                    callback.onResult(it.photos)
                    updateState(DataSourceState.NEXT_DONE)
                }, {
                    updateState(DataSourceState.ERROR)
                })
    }

    private fun updateState(state: DataSourceState) {
        this.state.postValue(state)
    }
}