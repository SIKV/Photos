package com.github.sikv.photos

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager


class App : Application() {

    companion object {
        var instance: App? = null
            private set
    }

    val messageLiveData = MutableLiveData<Event<String>>()

    override fun onCreate() {
        super.onCreate()

        instance = this

        updateTheme()
    }

    fun updateTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        val nightModeEnabled = preferenceManager.getBoolean(getString(R.string.pref_dark_theme), true)

        AppCompatDelegate.setDefaultNightMode(if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }
}