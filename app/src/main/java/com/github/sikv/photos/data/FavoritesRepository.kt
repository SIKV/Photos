package com.github.sikv.photos.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.database.FavoritesDao
import com.github.sikv.photos.model.*
import com.github.sikv.photos.util.AccountManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
        private val accountManager: AccountManager,
        private val favoritesDao: FavoritesDao
) {

    companion object {
        private const val FAVORITES_COLLECTION = "Favorites"
    }

    interface Callback {
        fun onFavoriteChanged(photo: Photo, favorite: Boolean)
        fun onFavoritesChanged()
    }

    private val subscribers = mutableListOf<Callback>()

    // This overrides default value for isFavorite flag for photos.
    // Only if [Add to Favorites], [Remove from Favorites] or [Delete All Favorites] action has been occurred.
    private val favorites = mutableMapOf<Photo, Boolean>()

    val favoritesLiveData: LiveData<List<FavoritePhotoEntity>> = Transformations.map(favoritesDao.getAll()) {
        it.forEach { photo ->
            photo.favorite = true
        }

        it
    }

    // Needed to UNDO Delete All action.
    private var deletedFavorites: List<FavoritePhotoEntity> = emptyList()

    fun subscribe(callback: Callback) {
        subscribers.add(callback)
    }

    fun unsubscribe(callback: Callback) {
        subscribers.remove(callback)
    }

    /**
     * Inverts isFavorite flag for [photo] and notifies all subscribers that [photo] has been changed.
     */
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

    fun deleteAll(completion: (Boolean) -> Unit) {
        GlobalScope.launch {
            val count = favoritesDao.getCount()

            if (count > 0) {
                deletedFavorites = favoritesDao.getAllList()
                favoritesDao.deleteAll()

                // We can't just call favorites.clear() because in that case isFavorite() method will return
                favorites.keys.forEach {
                    favorites[it] = false
                }

                subscribers.forEach {
                    it.onFavoritesChanged()
                }

                completion(true)

            } else {
                completion(false)
            }
        }
    }

    fun undoDeleteAll() {
        GlobalScope.launch {
            deletedFavorites.forEach { photo ->
                invertFavorite(photo)
            }

            deletedFavorites = emptyList()
        }
    }

    fun deleteAllFinally() {
        deletedFavorites = emptyList()
    }

    /**
     * Used to get [photo]'s isFavorite flag. If [photo] is contained in [favorites] map
     * then this function returns overridden local result from the map, if not, the initial one.
     */
    fun isFavorite(photo: Photo?): Boolean {
        return favorites[photo] ?: photo?.favorite ?: false
    }

    fun isFavoriteFromDatabase(photo: Photo): Boolean {
        return favoritesDao.getById(photo.getPhotoId()) != null
    }

    fun populateFavorite(photos: List<UnsplashPhoto>): List<UnsplashPhoto> {
        photos.forEach {
            it.favorite = isFavoriteFromDatabase(it)
        }
        return photos
    }

    fun populateFavorite(photo: UnsplashPhoto): UnsplashPhoto {
        photo.favorite = isFavoriteFromDatabase(photo)
        return photo
    }

    fun populateFavorite(response: UnsplashSearchResponse): UnsplashSearchResponse {
        response.results.forEach {
            it.favorite = isFavoriteFromDatabase(it)
        }
        return response
    }

    fun populateFavorite(response: PexelsCuratedPhotosResponse): PexelsCuratedPhotosResponse {
        response.photos.forEach {
            it.favorite = isFavoriteFromDatabase(it)
        }
        return response
    }

    fun populateFavorite(photo: PexelsPhoto): PexelsPhoto {
        photo.favorite = isFavoriteFromDatabase(photo)
        return photo
    }

    fun populateFavorite(response: PexelsSearchResponse): PexelsSearchResponse {
        response.photos.forEach {
            it.favorite = isFavoriteFromDatabase(it)
        }
        return response
    }

    fun sync() {
        // TODO Implement
    }
}