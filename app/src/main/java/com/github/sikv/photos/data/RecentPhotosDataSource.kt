package com.github.sikv.photos.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.UnsplashPhoto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentPhotosDataSource(
        private val unsplashClient: UnsplashClient

) : PositionalDataSource<Photo>() {

    var state: MutableLiveData<DataSourceState> = MutableLiveData()
        private set

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(DataSourceState.LOADING_INITIAL)

        unsplashClient.getLatestPhotos(params.requestedStartPosition, params.requestedLoadSize)
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

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(DataSourceState.LOADING)

        unsplashClient.getLatestPhotos(params.startPosition, params.loadSize)
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

    private fun updateState(state: DataSourceState) {
        this.state.postValue(state)
    }
}