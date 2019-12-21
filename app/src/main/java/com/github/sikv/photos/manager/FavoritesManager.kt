package com.github.sikv.photos.manager

import com.github.sikv.photos.model.UnsplashPhoto
import javax.inject.Inject

class FavoritesManager @Inject constructor() {

    fun populateFavorite(photos: List<UnsplashPhoto>): List<UnsplashPhoto> {
        return photos
    }
}