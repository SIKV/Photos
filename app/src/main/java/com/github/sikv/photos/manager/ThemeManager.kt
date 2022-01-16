package com.github.sikv.photos.manager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.github.sikv.photos.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    fun applyTheme() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        val nightModeEnabled = preferenceManager.getBoolean(context.getString(R.string._pref_dark_theme), true)

        AppCompatDelegate.setDefaultNightMode(
            if (nightModeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
