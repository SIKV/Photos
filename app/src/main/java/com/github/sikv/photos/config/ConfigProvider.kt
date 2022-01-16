package com.github.sikv.photos.config

import androidx.paging.PagingConfig
import com.github.sikv.photos.model.PhotoSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigProvider @Inject constructor() {

    fun getSearchSources(): List<PhotoSource> {
        return listOf(
            PhotoSource.PEXELS,
            PhotoSource.UNSPLASH,
            PhotoSource.PIXABAY
        )
    }

    fun getPagingConfig(): PagingConfig {
        return PagingConfig(
            initialLoadSize = 10,
            pageSize = 10,
            enablePlaceholders = false
        )
    }

    fun getRecommendationsLimit(): Int = 10
}
