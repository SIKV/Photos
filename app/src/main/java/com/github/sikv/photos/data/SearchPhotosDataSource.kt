package com.github.sikv.photos.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.github.sikv.photos.api.PhotosClient
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.SearchPhotosResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPhotosDataSource(
        private val photosClient: PhotosClient,
        private val searchQuery: String

) : PositionalDataSource<Photo>() {

    var state: MutableLiveData<State> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        updateState(State.LOADING)

        photosClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)
                .enqueue(object : Callback<SearchPhotosResponse> {
                    override fun onFailure(call: Call<SearchPhotosResponse>?, t: Throwable?) {
                        updateState(State.ERROR)
                    }

                    override fun onResponse(call: Call<SearchPhotosResponse>?, response: Response<SearchPhotosResponse>?) {
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

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        updateState(State.LOADING)

        photosClient.searchPhotos(searchQuery, params.startPosition, params.loadSize)
                .enqueue(object : Callback<SearchPhotosResponse> {
                    override fun onFailure(call: Call<SearchPhotosResponse>?, t: Throwable?) {
                        updateState(State.ERROR)
                    }

                    override fun onResponse(call: Call<SearchPhotosResponse>?, response: Response<SearchPhotosResponse>?) {
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