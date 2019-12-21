package com.github.sikv.photos.manager

import com.github.sikv.photos.database.FavoritesDao
import com.github.sikv.photos.model.*
import javax.inject.Inject

// TODO Optimize
class FavoritesManager @Inject constructor(private val favoritesDao: FavoritesDao) {

    fun populateFavorite(photos: List<UnsplashPhoto>): List<UnsplashPhoto> {
        photos.forEach {
            it.favorite = isFavoritePhoto(it)
        }
        return photos
    }

    fun populateFavorite(photo: UnsplashPhoto): UnsplashPhoto {
        photo.favorite = isFavoritePhoto(photo)
        return photo
    }

    fun populateFavorite(response: UnsplashSearchResponse): UnsplashSearchResponse {
        response.results.forEach {
            it.favorite = isFavoritePhoto(it)
        }
        return response
    }

    fun populateFavorite(response: PexelsCuratedPhotosResponse): PexelsCuratedPhotosResponse {
        response.photos.forEach {
            it.favorite = isFavoritePhoto(it)
        }
        return response
    }

    fun populateFavorite(photo: PexelsPhoto): PexelsPhoto {
        photo.favorite = isFavoritePhoto(photo)
        return photo
    }

    fun populateFavorite(response: PexelsSearchResponse): PexelsSearchResponse {
        response.photos.forEach {
            it.favorite = isFavoritePhoto(it)
        }
        return response
    }

    private fun isFavoritePhoto(photo: Photo): Boolean {
        return favoritesDao.getById(photo.getPhotoId()) != null
    }
}