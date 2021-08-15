package com.github.sikv.photos.config

import androidx.paging.PagingConfig
import com.github.sikv.photos.enumeration.SearchSource

object ListConfig {

    val pagingConfig = PagingConfig(
            initialLoadSize = 10,
            pageSize = 10,
            enablePlaceholders = false
    )

    val RECOMMENDATIONS_LIMIT = 9 * SearchSource.size
}