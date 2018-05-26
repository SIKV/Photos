package com.github.sikv.photos.data

import android.arch.lifecycle.LiveData
import com.github.sikv.photos.util.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitLiveData<T>(private val call: Call<T>) : LiveData<T>(), Callback<T> {

    override fun onActive() {
        if (!call.isCanceled && !call.isExecuted) {
            call.enqueue(this)
        }
    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        Utils.log("onFailure. ${t?.message}")
    }

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        value = response?.body()
    }
}