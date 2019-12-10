package com.github.sikv.photos

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
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

    val downloadPhotoStateLiveData = MutableLiveData<DownloadPhotoState>()
    val setWallpaperStateLiveData = MutableLiveData<SetWallpaperState>()

    override fun onCreate() {
        super.onCreate()

        instance = this

        updateTheme()
    }

    fun updateTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        val nightModeEnabled = preferenceManager.getBoolean(getString(R.string._pref_dark_theme), true)

        AppCompatDelegate.setDefaultNightMode(if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }
}