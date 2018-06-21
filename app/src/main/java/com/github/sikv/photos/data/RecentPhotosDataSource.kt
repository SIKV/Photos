package com.github.sikv.photos.data

import android.arch.paging.PositionalDataSource
import com.github.sikv.photos.model.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentPhotosDataSource(
        private val photosStorage: PhotosHandler

) : PositionalDataSource<Photo>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Photo>) {

        photosStorage.getLatestPhotos(params.requestedStartPosition, params.requestedLoadSize)
                .enqueue(object : Callback<List<Photo>> {
                    override fun onFailure(call: Call<List<Photo>>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<List<Photo>>?, response: Response<List<Photo>>?) {
                        response?.let {

                            callback.onResult(response.body()!!, 0)

                        }
                    }
                })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {

        photosStorage.getLatestPhotos(params.startPosition, params.loadSize)
                .enqueue(object : Callback<List<Photo>> {
                    override fun onFailure(call: Call<List<Photo>>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<List<Photo>>?, response: Response<List<Photo>>?) {
                        response?.let {
                            callback.onResult(response.body()!!)
                        }
                    }
                })
    }
}