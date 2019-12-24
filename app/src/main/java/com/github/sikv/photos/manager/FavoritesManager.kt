package com.github.sikv.photos.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.database.FavoritesDao
import com.github.sikv.photos.model.*
import com.github.sikv.photos.util.Event
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesManager @Inject constructor(private val favoritesDao: FavoritesDao) {

    interface Callback {
        fun onFavoriteChanged(photo: Photo, favorite: Boolean)
        fun onFavoritesChanged()
    }

    private val subscribers = mutableListOf<Callback>()

    private val favorites = mutableMapOf<Photo, Boolean>()

    private var deletedFavorites: List<FavoritePhotoEntity> = emptyList()

    private val deleteAllMutableEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val deleteAllEvent: LiveData<Event<Boolean>> = deleteAllMutableEvent

    fun subscribe(callback: Callback) {
        subscribers.add(callback)
    }

    fun unsubscribe(callback: Callback) {
        subscribers.remove(callback)
    }

    fun invertFavorite(photo: Photo) {
        val favorite = !isFavorite(photo)

        favorites[photo] = favorite

        subscribers.forEach {
            it.onFavoriteChanged(photo, favorite)
        }

        val favoritePhoto = FavoritePhotoEntity.fromPhoto(photo)

        GlobalScope.launch {
            if (favorite) {
                favoritesDao.insert(favoritePhoto)
            } else {
                favoritesDao.delete(favoritePhoto)
            }
        }
    }

    fun deleteAll() {
        GlobalScope.launch {
            val count = favoritesDao.getCount()

            if (count > 0) {
                deletedFavorites = favoritesDao.getAllList()
                favoritesDao.deleteAll()

                deleteAllMutableEvent.postValue(Event(true))

                favorites.clear()

                subscribers.forEach {
                    it.onFavoritesChanged()
                }

            } else {
                deleteAllMutableEvent.postValue(Event(false))
            }
        }
    }

    fun undoDeleteAll() {
        GlobalScope.launch {
            deletedFavorites.forEach { photo ->
                favoritesDao.insert(photo)
            }

            deletedFavorites = emptyList()
        }
    }

    fun deleteAllFinally() {
        deletedFavorites = emptyList()
    }

    fun isFavorite(photo: Photo?): Boolean {
        return favorites[photo] ?: photo?.isFavoritePhoto() ?: false
    }

    fun populateFavorite(photos: List<UnsplashPhoto>): List<UnsplashPhoto> {
        photos.forEach {
            it.setIsFavorite(isFavoritePhoto(it))
        }
        return photos
    }

    fun populateFavorite(photo: UnsplashPhoto): UnsplashPhoto {
        photo.setIsFavorite(isFavoritePhoto(photo))
        return photo
    }

    fun populateFavorite(response: UnsplashSearchResponse): UnsplashSearchResponse {
        response.results.forEach {
            it.setIsFavorite(isFavoritePhoto(it))
        }
        return response
    }

    fun populateFavorite(response: PexelsCuratedPhotosResponse): PexelsCuratedPhotosResponse {
        response.photos.forEach {
            it.setIsFavorite(isFavoritePhoto(it))
        }
        return response
    }

    fun populateFavorite(photo: PexelsPhoto): PexelsPhoto {
        photo.setIsFavorite(isFavoritePhoto(photo))
        return photo
    }

    fun populateFavorite(response: PexelsSearchResponse): PexelsSearchResponse {
        response.photos.forEach {
            it.setIsFavorite(isFavoritePhoto(it))
        }
        return response
    }

    private fun isFavoritePhoto(photo: Photo): Boolean {
        return favoritesDao.getById(photo.getPhotoId()) != null
    }
}