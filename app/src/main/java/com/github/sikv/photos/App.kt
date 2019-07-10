package com.github.sikv.photos

import android.app.Application
import androidx.lifecycle.MutableLiveData


class App : Application() {

    companion object {
        var instance: App? = null
            private set
    }

    val messageLiveData = MutableLiveData<Event<String>>()

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}