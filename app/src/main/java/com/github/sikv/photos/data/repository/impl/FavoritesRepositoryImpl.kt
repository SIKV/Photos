package com.github.sikv.photos.data.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.storage.FavoritesDbQueryBuilder
import com.github.sikv.photos.data.storage.FavoritesDao
import com.github.sikv.photos.data.storage.FavoritePhotoEntity
import com.github.sikv.photos.enumeration.SortBy
import com.github.sikv.photos.model.Photo
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val queryBuilder: FavoritesDbQueryBuilder
) : FavoritesRepository {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val subscribers = mutableListOf<FavoritesRepository.Listener>()

    /**
     * This overrides default value for isFavorite flag for photos.
     * Only if [Add to Favorites], [Remove from Favorites] or [Delete All Favorites] action has been occurred.
     */
    private val favorites = mutableMapOf<Photo, Boolean>()

    override fun subscribe(listener: FavoritesRepository.Listener) {
        subscribers.add(listener)
    }

    override fun unsubscribe(listener: FavoritesRepository.Listener) {
        subscribers.remove(listener)
    }

    override fun getFavoritesLiveData(sortBy: SortBy): LiveData<List<FavoritePhotoEntity>> {
        val query = queryBuilder.buildGetPhotosQuery(sortBy)

        return Transformations.map(favoritesDao.getPhotos(query)) { photos ->
            photos.onEach { it.favorite = true }
        }
    }

    /**
     * Inverts isFavorite flag for [photo] and notifies all subscribers that [photo] has been changed.
     */
    override fun invertFavorite(photo: Photo) {
        val favorite = !isFavorite(photo)

        favorites[photo] = favorite

        subscribers.forEach {
            it.onFavoriteChanged(photo, favorite)
        }

        val favoritePhoto = FavoritePhotoEntity.fromPhoto(photo)

        scope.launch {
            if (favorite) {
                favoritesDao.insert(favoritePhoto)
            } else {
                favoritesDao.delete(favoritePhoto)
            }
        }
    }

    override suspend fun markAllAsRemoved(): Boolean = withContext(Dispatchers.IO) {
        val count = favoritesDao.getCount()

        if (count > 0) {
            favoritesDao.markAllAsDeleted()

            favorites.keys.forEach {
                favorites[it] = false
            }

            subscribers.forEach {
                it.onFavoritesChanged()
            }

            true
        } else {
            false
        }
    }

    override fun unmarkAllAsRemoved() {
        scope.launch {
            favoritesDao.unmarkAllAsDeleted()

            favorites.keys.forEach {
                favorites[it] = true
            }

            subscribers.forEach {
                it.onFavoritesChanged()
            }
        }
    }

    override fun removeAll() {
        scope.launch {
            favoritesDao.deleteAll()
        }
    }

    /**
     * Used to get [photo]'s isFavorite flag. If [photo] is contained in [favorites] map
     * then this function returns overridden local result from the map, if not, the initial one.
     */
    override fun isFavorite(photo: Photo?): Boolean = favorites[photo] ?: photo?.favorite ?: false

    override suspend fun isFavoriteFromDatabase(photo: Photo): Boolean = withContext(Dispatchers.IO) {
        favoritesDao.getById(photo.getPhotoId()) != null
    }

    override suspend fun populateFavorite(photo: Photo): Photo {
        photo.favorite = isFavoriteFromDatabase(photo)
        return photo
    }
}