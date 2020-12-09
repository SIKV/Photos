package com.github.sikv.photos.enumeration

enum class SearchSource(val photoSource: PhotoSource) {
    PEXELS(PhotoSource.PEXELS),
    UNSPLASH(PhotoSource.UNSPLASH),
    PIXABAY(PhotoSource.PIXABAY);

    companion object {
        val size = values().size

        fun getAt(position: Int) = values()[position]
    }
}