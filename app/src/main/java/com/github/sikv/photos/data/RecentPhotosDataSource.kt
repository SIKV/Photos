package com.github.sikv.photos.data

import android.arch.paging.PositionalDataSource
import android.util.Log
import com.github.sikv.photos.api.PhotosClient
import com.github.sikv.photos.model.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentPhotosDataSource(
        private val photosClient: PhotosClient

) : PositionalDataSource<Photo>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {
        photosClient.getLatestPhotos(params.requestedStartPosition, params.requestedLoadSize)
                .enqueue(object : Callback<List<Photo>> {
                    override fun onFailure(call: Call<List<Photo>>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<List<Photo>>?, response: Response<List<Photo>>?) {
                        response?.body()?.let {
                            callback.onResult(it, 0)
                        }
                    }
                })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
        photosClient.getLatestPhotos(params.startPosition, params.loadSize)
                .enqueue(object : Callback<List<Photo>> {
                    override fun onFailure(call: Call<List<Photo>>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<List<Photo>>?, response: Response<List<Photo>>?) {
                        response?.body()?.let {
                            callback.onResult(it)
                        }
                    }
                })
    }
}