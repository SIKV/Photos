package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.model.PexelsCuratedPhotosResponse
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashPhoto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
                        .enqueue(object : Callback<List<UnsplashPhoto>> {
                            override fun onFailure(call: Call<List<UnsplashPhoto>>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<List<UnsplashPhoto>>?, response: Response<List<UnsplashPhoto>>?) {
                                response?.body()?.let {
                                    callback.onResult(it, 0)
                                    updateState(DataSourceState.DONE)

                                } ?: run {
                                    updateState(DataSourceState.ERROR)
                                }
                            }
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.getCuratedPhotos(params.requestedStartPosition, params.requestedLoadSize)
                        .enqueue(object : Callback<PexelsCuratedPhotosResponse> {
                            override fun onFailure(call: Call<PexelsCuratedPhotosResponse>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<PexelsCuratedPhotosResponse>?, response: Response<PexelsCuratedPhotosResponse>?) {
                                response?.body()?.let {
                                    callback.onResult(it.photos, 0)
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

        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                apiClient.unsplashClient.getLatestPhotos(params.startPosition, params.loadSize)
                        .enqueue(object : Callback<List<UnsplashPhoto>> {
                            override fun onFailure(call: Call<List<UnsplashPhoto>>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<List<UnsplashPhoto>>?, response: Response<List<UnsplashPhoto>>?) {
                                response?.body()?.let {
                                    callback.onResult(it)
                                    updateState(DataSourceState.DONE)

                                } ?: run {
                                    updateState(DataSourceState.ERROR)
                                }
                            }
                        })
            }

            PhotoSource.PEXELS -> {
                apiClient.pexelsClient.getCuratedPhotos(params.startPosition, params.loadSize)
                        .enqueue(object : Callback<PexelsCuratedPhotosResponse> {
                            override fun onFailure(call: Call<PexelsCuratedPhotosResponse>?, t: Throwable?) {
                                updateState(DataSourceState.ERROR)
                            }

                            override fun onResponse(call: Call<PexelsCuratedPhotosResponse>?, response: Response<PexelsCuratedPhotosResponse>?) {
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