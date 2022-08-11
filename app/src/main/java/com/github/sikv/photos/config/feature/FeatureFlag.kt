package com.github.sikv.photos.config.feature

enum class FeatureFlag(
    val key: String,
) {
    SEARCH_SOURCE_PEXELS("searchSourcePexels"),
    SEARCH_SOURCE_UNSPLASH("searchSourceUnsplash"),
    SEARCH_SOURCE_PIXABAY("searchSourcePixabay"),

    RECOMMENDATIONS("recommendations")
}
