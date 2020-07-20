package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.BuildConfig
import com.github.sikv.photos.event.Event

class SettingsViewModel: ViewModel() {

    private val showAppVersionMutableEvent = MutableLiveData<Event<String>>()
    val showAppVersionEvent: LiveData<Event<String>> = showAppVersionMutableEvent

    init {
        val appVersion = BuildConfig.VERSION_NAME
        showAppVersionMutableEvent.postValue(Event(appVersion))
    }
}