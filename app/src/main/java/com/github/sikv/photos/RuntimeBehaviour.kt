package com.github.sikv.photos

import androidx.appcompat.app.AppCompatActivity
import com.github.sikv.photos.config.Config
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.config.RemoteConfigProvider
import com.github.sikv.photos.ui.dialog.FullScreenLoadingDialog
import com.github.sikv.photos.util.NotInitializedException

object RuntimeBehaviour {

    private const val KEY_CONFIG_FETCHED = "configFetched"

    private var configProvider: ConfigProvider? = null

    fun init(activity: AppCompatActivity, doAfter: () -> Unit) {
        configProvider = RemoteConfigProvider()

        if (!isConfigFetched()) {
            val dialog = FullScreenLoadingDialog()
            dialog.show(activity.supportFragmentManager, null)

            configProvider?.fetch {
                dialog.dismiss()
                doAfter()

                configFetched()
            }
        } else {
            doAfter()
            configProvider?.refresh()
        }
    }

    fun getConfig(config: Config): Boolean {
        configProvider?.let {
            return it.getConfig(config)
        } ?: run {
            throw NotInitializedException("RuntimeBehaviour is not initialized")
        }
    }

    private fun isConfigFetched(): Boolean {
        val preferences = App.instance.getPrivatePreferences()
        return preferences.getBoolean(KEY_CONFIG_FETCHED, false)
    }

    private fun configFetched() {
        val preferences = App.instance.getPrivatePreferences()
        preferences.edit().putBoolean(KEY_CONFIG_FETCHED, true).apply()
    }
}