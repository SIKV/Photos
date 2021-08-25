package com.github.sikv.photos.config

interface ConfigProvider {
    fun fetch(doAfter: () -> Unit)
    fun refresh()
    fun isFeatureEnabled(featureFlag: FeatureFlag): Boolean
}