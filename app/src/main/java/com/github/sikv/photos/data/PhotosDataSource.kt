package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.subscribeAsync

class PhotosDataSource(
        private val apiClient: ApiClient,
        private val photoSource: PhotoSource
) : PositionalDataSource<Photo>() {

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                apiClient.unsplashClient.getLatestPhotos(params.requestedStartPosition, params.requestedLoadSize)
                        .subscribeAsync({
                            callback.onResult(it, 0)
                            updateState(DataSourceState.INITIAL_LOADING_DONE)
                        }, {
                            updateState(DataSourceState.ERROR)
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.getCuratedPhotos(params.requestedStartPosition, params.requestedLoadSize)
                        .subscribeAsync({
                            callback.onResult(it.photos, 0)
                            updateState(DataSourceState.INITIAL_LOADING_DONE)
                        }, {
                            updateState(DataSourceState.ERROR)
                        })
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(DataSourceState.LOADING_NEXT)

        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                apiClient.unsplashClient.getLatestPhotos(params.startPosition, params.loadSize)
                        .subscribeAsync({
                            callback.onResult(it)
                            updateState(DataSourceState.NEXT_DONE)
                        }, {
                            updateState(DataSourceState.ERROR)
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.getCuratedPhotos(params.startPosition, params.loadSize)
                        .subscribeAsync({
                            callback.onResult(it.photos)
                            updateState(DataSourceState.NEXT_DONE)
                        }, {
                            updateState(DataSourceState.ERROR)
                        })
            }
        }
    }

    private fun updateState(state: DataSourceState) {
        this.state.postValue(state)
    }
}