package com.github.sikv.photos

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.preference.PreferenceManager
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.di.component.AppComponent
import com.github.sikv.photos.di.component.DaggerAppComponent
import com.github.sikv.photos.di.component.DaggerNetworkComponent
import com.github.sikv.photos.di.component.NetworkComponent
import com.github.sikv.photos.util.DownloadPhotoState
import com.github.sikv.photos.util.SetWallpaperState

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    val networkComponent: NetworkComponent by lazy {
        DaggerNetworkComponent.factory().create()
    }

    val messageLiveData = MutableLiveData<Event<String>>()

    private val downloadPhotoStateMutableLiveData = MutableLiveData<DownloadPhotoState>()
    private val setWallpaperStateMutableLiveData = MutableLiveData<SetWallpaperState>()

    val downloadPhotoStateLiveData: LiveData<DownloadPhotoState> = Transformations.map(downloadPhotoStateMutableLiveData) { state ->
        when (state) {
            // PHOTO_READY and ERROR_DOWNLOADING_PHOTO states should be handled only once.
            // For example, if PhotoActivity handled it then it SHOULD NOT be handled by MainActivity and vice versa.
            DownloadPhotoState.PHOTO_READY, DownloadPhotoState.ERROR_DOWNLOADING_PHOTO -> {
                downloadPhotoStateMutableLiveData.value = null
                state
            }

            else -> state
        }
    }

    val setWallpaperStateLiveData: LiveData<SetWallpaperState> = setWallpaperStateMutableLiveData

    override fun onCreate() {
        super.onCreate()

        instance = this

        updateTheme()
    }

    fun postDownloadPhotoStateLiveData(state: DownloadPhotoState) {
        downloadPhotoStateMutableLiveData.postValue(state)
    }

    fun postSetWallpaperStateLiveData(state: SetWallpaperState) {
        setWallpaperStateMutableLiveData.postValue(state)
    }

    fun updateTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        val nightModeEnabled = preferenceManager.getBoolean(getString(R.string._pref_dark_theme), true)

        AppCompatDelegate.setDefaultNightMode(if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }
}