package com.github.sikv.photos.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig

class RemoteFeatureFlagRepository : FeatureFlagRepository {

    private val remoteConfig = Firebase.remoteConfig

    init {
        remoteConfig.setDefaultsAsync(R.xml.feature_flag_defaults)
    }

    override fun fetch(doAfter: () -> Unit) {
        remoteConfig.fetch(0).addOnCompleteListener {
            remoteConfig.activate().addOnCompleteListener {
                doAfter()
            }
        }
    }

    /**
     * Refreshed configs will be available after the app is restarted.
     *
     * From docs: The default minimum fetch interval for Remote Config is 12 hours,
     * which means that configs won't be fetched from the backend more than once in a 12 hour window,
     * regardless of how many fetch calls are actually made.
     */
    override fun refresh() {
        remoteConfig.fetchAndActivate()
    }

    override fun isFeatureEnabled(featureFlag: FeatureFlag): Boolean {
        return remoteConfig.getBoolean(featureFlag.key)
    }
}
