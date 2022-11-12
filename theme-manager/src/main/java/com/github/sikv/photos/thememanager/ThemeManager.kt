package com.github.sikv.photos.thememanager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = "theme"

    private val preferences = context.getSharedPreferences("ThemePreferences", Context.MODE_PRIVATE)

    fun getThemes(): List<AppTheme> {
        return AppTheme.values().toList()
    }

    fun getCurrentTheme(): AppTheme {
        val themeId = preferences.getInt(themeKey, AppTheme.FollowSystem.id)

        return AppTheme.values().find { theme ->
            theme.id == themeId
        } ?: AppTheme.FollowSystem
    }

    fun applyTheme(theme: AppTheme) {
        val mode = when (theme) {
            AppTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.FollowSystem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)

        preferences
            .edit()
            .putInt(themeKey, theme.id)
            .apply()
    }
}
