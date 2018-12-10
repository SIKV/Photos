package com.github.sikv.photos.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.model.UnsplashSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPhotosDataSource(
        private val unsplashClient: UnsplashClient,
        private val searchQuery: String

) : PositionalDataSource<UnsplashPhoto>() {

    var state: MutableLiveData<State> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<UnsplashPhoto>) {
        updateState(State.LOADING)

        unsplashClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)
                .enqueue(object : Callback<UnsplashSearchResponse> {
                    override fun onFailure(call: Call<UnsplashSearchResponse>?, t: Throwable?) {
                        updateState(State.ERROR)
                    }

                    override fun onResponse(call: Call<UnsplashSearchResponse>?, response: Response<UnsplashSearchResponse>?) {
                        response?.body()?.let {
                            val res = it.results
                            callback.onResult(res,0, res.size)

                            updateState(State.DONE)

                        } ?: run {
                            updateState(State.ERROR)
                        }
                    }
                })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<UnsplashPhoto>) {
        updateState(State.LOADING)

        unsplashClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)
                .enqueue(object : Callback<UnsplashSearchResponse> {
                    override fun onFailure(call: Call<UnsplashSearchResponse>?, t: Throwable?) {
                        updateState(State.ERROR)
                    }

                    override fun onResponse(call: Call<UnsplashSearchResponse>?, response: Response<UnsplashSearchResponse>?) {
                        response?.body()?.let {
                            callback.onResult(it.results)

                            updateState(State.DONE)

                        } ?: run {
                            updateState(State.ERROR)
                        }
                    }
                })
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }
}