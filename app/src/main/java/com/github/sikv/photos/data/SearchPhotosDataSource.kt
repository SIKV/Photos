package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.subscribeAsync

class SearchPhotosDataSource(
        private val apiClient: ApiClient,
        private val photoSource: PhotoSource,
        private val searchQuery: String
) : PositionalDataSource<Photo>() {

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                apiClient.unsplashClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)
                        .subscribeAsync({
                            val res = it.results
                            callback.onResult(res,0, res.size)

                            updateState(DataSourceState.INITIAL_LOADING_DONE)
                        }, {
                            updateState(DataSourceState.ERROR)
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)
                        .subscribeAsync({
                            val res = it.photos
                            callback.onResult(res,0, res.size)

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
                apiClient.unsplashClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)
                        .subscribeAsync({
                            callback.onResult(it.results)

                            updateState(DataSourceState.NEXT_DONE)
                        }, {
                            updateState(DataSourceState.ERROR)
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)
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