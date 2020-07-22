package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchPhotosDataSource(
        private val apiClient: ApiClient,
        private val photoSource: PhotoSource,
        private val searchQuery: String
) : PositionalDataSource<Photo>() {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        scope.launch {
            when (photoSource) {
                PhotoSource.UNSPLASH -> {
                    try {
                        val result = apiClient.unsplashClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)

                        callback.onResult(result.results, 0, result.results.size)
                        updateState(DataSourceState.INITIAL_LOADING_DONE)

                    } catch (e: Exception) {
                        updateState(DataSourceState.ERROR)
                    }
                }

                PhotoSource.PEXELS -> {
                    try {
                        val result = apiClient.pexelsClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)

                        callback.onResult(result.photos, 0, result.photos.size)
                        updateState(DataSourceState.INITIAL_LOADING_DONE)

                    } catch (e: Exception) {
                        updateState(DataSourceState.ERROR)
                    }
                }

                else -> updateState(DataSourceState.ERROR)
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(DataSourceState.LOADING_NEXT)

        scope.launch {
            when (photoSource) {
                PhotoSource.UNSPLASH -> {
                    try {
                        val result = apiClient.unsplashClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)

                        callback.onResult(result.results)
                        updateState(DataSourceState.NEXT_DONE)

                    } catch (e: Exception) {
                        updateState(DataSourceState.ERROR)
                    }
                }

                PhotoSource.PEXELS -> {
                    try {
                        val result = apiClient.pexelsClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)

                        callback.onResult(result.photos)
                        updateState(DataSourceState.NEXT_DONE)

                    } catch (e: Exception) {
                        updateState(DataSourceState.ERROR)
                    }
                }

                else ->  updateState(DataSourceState.ERROR)
            }
        }
    }

    private fun updateState(state: DataSourceState) {
        this.state.postValue(state)
    }
}