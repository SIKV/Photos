package com.github.sikv.photos

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        DynamicColors.applyToActivitiesIfAvailable(this)
        updateTheme()
    }

    fun updateTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeEnabled = preferenceManager.getBoolean(getString(R.string._pref_dark_theme), true)
        AppCompatDelegate.setDefaultNightMode(if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }
}
