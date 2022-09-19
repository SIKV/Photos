package com.github.sikv.photos

import android.app.Application
import com.github.sikv.photos.util.ThemeManager
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        themeManager.applyTheme()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
