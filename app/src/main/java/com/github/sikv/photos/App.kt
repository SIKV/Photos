package com.github.sikv.photos

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.preference.PreferenceManager
import com.github.sikv.photos.di.component.AppComponent
import com.github.sikv.photos.di.component.DaggerAppComponent
import com.github.sikv.photos.di.component.DaggerNetworkComponent
import com.github.sikv.photos.di.component.NetworkComponent
import com.github.sikv.photos.enumeration.DownloadPhotoState
import com.github.sikv.photos.enumeration.SetWallpaperState
import com.github.sikv.photos.event.Event

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    val networkComponent: NetworkComponent by lazy {
        DaggerNetworkComponent.factory().create(applicationContext)
    }

    private val messageMutableEvent = MutableLiveData<Event<String>>()
    val messageEvent: LiveData<Event<String>> = messageMutableEvent

    private val downloadPhotoStateChangedMutableLiveData = MutableLiveData<DownloadPhotoState>()

    val downloadPhotoStateChangedLiveData: LiveData<DownloadPhotoState> = Transformations.map(downloadPhotoStateChangedMutableLiveData) { state ->
        when (state) {
            // PHOTO_READY and ERROR_DOWNLOADING_PHOTO states should be handled only once.
            // For example, if PhotoActivity handled it then it SHOULD NOT be handled by MainActivity and vice versa.
            DownloadPhotoState.PHOTO_READY, DownloadPhotoState.ERROR_DOWNLOADING_PHOTO -> {
                downloadPhotoStateChangedMutableLiveData.value = null
                state
            }

            else -> state
        }
    }

    private val setWallpaperStateChangedMutableEvent = MutableLiveData<Event<SetWallpaperState>>()
    val setWallpaperStateChangedEvent: LiveData<Event<SetWallpaperState>> = setWallpaperStateChangedMutableEvent

    override fun onCreate() {
        super.onCreate()

        instance = this

        updateTheme()
    }

    fun getPrivatePreferences(): SharedPreferences {
        return getSharedPreferences("Preferences", Context.MODE_PRIVATE)
    }

    fun postDownloadPhotoState(state: DownloadPhotoState) {
        downloadPhotoStateChangedMutableLiveData.postValue(state)
    }

    fun postSetWallpaperState(state: SetWallpaperState) {
        setWallpaperStateChangedMutableEvent.postValue(Event(state))
    }

    fun updateTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        val nightModeEnabled = preferenceManager.getBoolean(getString(R.string._pref_dark_theme), true)

        AppCompatDelegate.setDefaultNightMode(if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun postMessage(message: String?) {
        if (messageMutableEvent.hasActiveObservers()) {
            message?.let {
                messageMutableEvent.postValue(Event(it))
            }
        }
    }
}