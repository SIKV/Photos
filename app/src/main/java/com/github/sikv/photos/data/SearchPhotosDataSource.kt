package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.model.PexelsSearchResponse
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchPhotosDataSource(
        private val apiClient: ApiClient,
        private val photoSource: PhotoSource,
        private val searchQuery: String

) : PositionalDataSource<Photo>() {

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING)

        // TODO Refactor
        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                apiClient.unsplashClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)
                        .enqueue(object : Callback<UnsplashSearchResponse> {
                            override fun onFailure(call: Call<UnsplashSearchResponse>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<UnsplashSearchResponse>?, response: Response<UnsplashSearchResponse>?) {
                                response?.body()?.let {
                                    val res = it.results
                                    callback.onResult(res,0, res.size)

                                    updateState(DataSourceState.DONE)

                                } ?: run {
                                    updateState(DataSourceState.ERROR)
                                }
                            }
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)
                        .enqueue(object : Callback<PexelsSearchResponse> {
                            override fun onFailure(call: Call<PexelsSearchResponse>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<PexelsSearchResponse>?, response: Response<PexelsSearchResponse>?) {
                                response?.body()?.let {
                                    val res = it.photos
                                    callback.onResult(res,0, res.size)

                                    updateState(DataSourceState.DONE)

                                } ?: run {
                                    updateState(DataSourceState.ERROR)
                                }
                            }
                        })
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(DataSourceState.LOADING)

        // TODO Refactor
        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                apiClient.unsplashClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)
                        .enqueue(object : Callback<UnsplashSearchResponse> {
                            override fun onFailure(call: Call<UnsplashSearchResponse>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<UnsplashSearchResponse>?, response: Response<UnsplashSearchResponse>?) {
                                response?.body()?.let {
                                    callback.onResult(it.results)

                                    updateState(DataSourceState.DONE)

                                } ?: run {
                                    updateState(DataSourceState.ERROR)
                                }
                            }
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)
                        .enqueue(object : Callback<PexelsSearchResponse> {
                            override fun onFailure(call: Call<PexelsSearchResponse>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<PexelsSearchResponse>?, response: Response<PexelsSearchResponse>?) {
                                response?.body()?.let {
                                    callback.onResult(it.photos)

                                    updateState(DataSourceState.DONE)

                                } ?: run {
                                    updateState(DataSourceState.ERROR)
                                }
                            }
                        })
            }
        }
    }

    private fun updateState(state: DataSourceState) {
        this.state.postValue(state)
    }
}