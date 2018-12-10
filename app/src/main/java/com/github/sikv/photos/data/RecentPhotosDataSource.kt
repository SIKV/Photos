package com.github.sikv.photos.data

import android.arch.paging.PositionalDataSource
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.model.UnsplashPhoto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentPhotosDataSource(
        private val unsplashClient: UnsplashClient

) : PositionalDataSource<UnsplashPhoto>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<UnsplashPhoto>) {
        unsplashClient.getLatestPhotos(params.requestedStartPosition, params.requestedLoadSize)
                .enqueue(object : Callback<List<UnsplashPhoto>> {
                    override fun onFailure(call: Call<List<UnsplashPhoto>>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<List<UnsplashPhoto>>?, response: Response<List<UnsplashPhoto>>?) {
                        response?.body()?.let {
                            callback.onResult(it, 0)
                        }
                    }
                })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<UnsplashPhoto>) {
        unsplashClient.getLatestPhotos(params.startPosition, params.loadSize)
                .enqueue(object : Callback<List<UnsplashPhoto>> {
                    override fun onFailure(call: Call<List<UnsplashPhoto>>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<List<UnsplashPhoto>>?, response: Response<List<UnsplashPhoto>>?) {
                        response?.body()?.let {
                            callback.onResult(it)
                        }
                    }
                })
    }
}