package com.github.sikv.photos.config

interface FeatureFlagRepository {
    fun fetch(doAfter: () -> Unit)
    fun refresh()
    fun isFeatureEnabled(featureFlag: FeatureFlag): Boolean
}
