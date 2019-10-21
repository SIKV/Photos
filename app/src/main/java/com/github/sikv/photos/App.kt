package com.github.sikv.photos

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.github.sikv.photos.data.Event
import com.github.sikv.photos.di.component.ApiClientComponent
import com.github.sikv.photos.di.component.DaggerApiClientComponent
import com.github.sikv.photos.di.component.DaggerGlideComponent
import com.github.sikv.photos.di.component.GlideComponent
import com.github.sikv.photos.di.module.ApiClientModule
import com.github.sikv.photos.di.module.GlideModule
import com.github.sikv.photos.di.module.RetrofitModule

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var apiClientComponent: ApiClientComponent
        private set

    lateinit var glideComponent: GlideComponent
        private set

    val messageLiveData = MutableLiveData<Event<String>>()

    override fun onCreate() {
        super.onCreate()

        instance = this

        apiClientComponent = DaggerApiClientComponent
                .builder()
                .retrofitModule(RetrofitModule())
                .apiClientModule(ApiClientModule())
                .build()

        glideComponent = DaggerGlideComponent
                .builder()
                .glideModule(GlideModule(instance))
                .build()

        updateTheme()
    }

    fun updateTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        val nightModeEnabled = preferenceManager.getBoolean(getString(R.string.pref_dark_theme), true)

        AppCompatDelegate.setDefaultNightMode(if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }
}