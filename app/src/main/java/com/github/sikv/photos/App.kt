package com.github.sikv.photos

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.di.component.DaggerGlideComponent
import com.github.sikv.photos.di.component.DaggerNetworkComponent
import com.github.sikv.photos.di.component.GlideComponent
import com.github.sikv.photos.di.component.NetworkComponent
import com.github.sikv.photos.util.SetWallpaperState

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    val networkComponent: NetworkComponent by lazy {
        DaggerNetworkComponent.factory().create()
    }

    val glideComponent: GlideComponent by lazy {
        DaggerGlideComponent.factory().create(applicationContext)
    }

    val messageLiveData = MutableLiveData<Event<String>>()

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