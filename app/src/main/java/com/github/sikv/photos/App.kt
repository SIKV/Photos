package com.github.sikv.photos

import android.app.Application
import com.github.sikv.photos.thememanager.ThemeManager
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()

        themeManager.applyTheme(themeManager.getCurrentTheme())
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
