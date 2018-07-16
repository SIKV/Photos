package com.github.sikv.photos.data

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

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        photosClient.searchPhotos(searchQuery, params.requestedStartPosition, params.requestedLoadSize)
                .enqueue(object : Callback<SearchPhotosResponse> {
                    override fun onFailure(call: Call<SearchPhotosResponse>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<SearchPhotosResponse>?, response: Response<SearchPhotosResponse>?) {
                        response?.let {
                            val res = response.body()!!.results
                            callback.onResult(res,0, res.size)
                        }
                    }
                })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
    }
}