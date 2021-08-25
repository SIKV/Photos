package com.github.sikv.photos

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.config.FeatureFlag
import com.github.sikv.photos.ui.dialog.FullScreenLoadingDialog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuntimeBehaviour @Inject constructor(
        private val configProvider: ConfigProvider,
        private val preferences: SharedPreferences
) {

    private val keyConfigFetched = "configFetched"

    fun fetchConfig(activity: AppCompatActivity, doAfter: () -> Unit) {
        if (!isConfigFetched()) {
            val dialog = FullScreenLoadingDialog()
            dialog.show(activity.supportFragmentManager, null)

            configProvider.fetch {
                dialog.dismiss()
                doAfter()

                configFetched()
            }
        } else {
            doAfter()
            configProvider.refresh()
        }
    }

    fun isFeatureEnabled(featureFlag: FeatureFlag): Boolean {
        return configProvider.isFeatureEnabled(featureFlag)
    }

    private fun isConfigFetched(): Boolean {
        return preferences.getBoolean(keyConfigFetched, false)
    }

    private fun configFetched() {
        preferences.edit().putBoolean(keyConfigFetched, true).apply()
    }
}