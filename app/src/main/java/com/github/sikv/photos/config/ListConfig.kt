package com.github.sikv.photos.config

import com.github.sikv.photos.enumeration.SearchSource

object ListConfig {
    const val INITIAL_LOAD_SIZE = 10
    const val PAGE_SIZE = 10

    val RECOMMENDATIONS_LIMIT = 9 * SearchSource.size
}