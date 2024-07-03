package com.github.sikv.photos.config

import com.github.sikv.photos.domain.PhotoSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigProvider @Inject constructor(
    private val featureFlagProvider: FeatureFlagProvider
) {

    fun getSearchSources(): List<PhotoSource> {
        val sources = mutableListOf<PhotoSource>()

        if (featureFlagProvider.isFeatureEnabled(FeatureFlag.SEARCH_SOURCE_PEXELS)) {
            sources.add(PhotoSource.PEXELS)
        }
        if (featureFlagProvider.isFeatureEnabled(FeatureFlag.SEARCH_SOURCE_UNSPLASH)) {
            sources.add(PhotoSource.UNSPLASH)
        }
        if (featureFlagProvider.isFeatureEnabled(FeatureFlag.SEARCH_SOURCE_PIXABAY)) {
            sources.add(PhotoSource.PIXABAY)
        }

        return sources
    }

    fun getPageConfig(): PageConfig {
        return PageConfig(
            initialLoadSize = 10,
            pageSize = 10,
            enablePlaceholders = false
        )
    }

    fun getRecommendationsLimit(): Int = 24
}
