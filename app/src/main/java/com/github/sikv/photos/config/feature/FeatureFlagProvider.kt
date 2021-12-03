package com.github.sikv.photos.config.feature

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlagProvider @Inject constructor(
    private val repository: FeatureFlagRepository
) {
    fun isFeatureEnabled(featureFlag: FeatureFlag): Boolean {
        return repository.isFeatureEnabled(featureFlag)
    }
}
