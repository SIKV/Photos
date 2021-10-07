package com.github.sikv.photos

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.github.sikv.photos.event.Event
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    private val globalMessageMutableEvent = MutableLiveData<Event<String>>()
    val globalMessageEvent: LiveData<Event<String>> = globalMessageMutableEvent

    override fun onCreate() {
        super.onCreate()

        instance = this

        updateTheme()
    }

    fun getPrivatePreferences(): SharedPreferences {
        return getSharedPreferences("Preferences", Context.MODE_PRIVATE)
    }

    fun postGlobalMessage(message: String) {
        globalMessageMutableEvent.postValue(Event(message))
    }

    fun updateTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeEnabled = preferenceManager.getBoolean(getString(R.string._pref_dark_theme), true)
        AppCompatDelegate.setDefaultNightMode(if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }
}