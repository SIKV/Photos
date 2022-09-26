package com.github.sikv.photos.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    fun applyTheme(theme: String? = null) {
        val themeValue = theme ?: PreferenceManager.getDefaultSharedPreferences(context)
            .getString(context.getString(R.string._pref_theme), "0")

        val mode = when (themeValue) {
            "0" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> AppCompatDelegate.MODE_NIGHT_YES
            "2" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
