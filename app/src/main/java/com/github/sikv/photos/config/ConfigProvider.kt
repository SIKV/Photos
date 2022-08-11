package com.github.sikv.photos.config

import androidx.paging.PagingConfig
import com.github.sikv.photos.config.feature.FeatureFlag
import com.github.sikv.photos.config.feature.FeatureFlagProvider
import com.github.sikv.photos.model.PhotoSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigProvider @Inject constructor(
    private val featureFlagProvider: FeatureFlagProvider
) {

    fun getSearchSources(): Set<PhotoSource> {
        val sources = mutableSetOf<PhotoSource>()

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

    fun getPagingConfig(): PagingConfig {
        return PagingConfig(
            initialLoadSize = 10,
            pageSize = 10,
            enablePlaceholders = false
        )
    }

    fun getRecommendationsLimit(): Int = 24
}
