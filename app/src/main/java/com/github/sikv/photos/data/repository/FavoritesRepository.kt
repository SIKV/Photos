package com.github.sikv.photos.data.repository

import androidx.lifecycle.LiveData
import com.github.sikv.photos.data.storage.FavoritePhotoEntity
import com.github.sikv.photos.enumeration.SortBy
import com.github.sikv.photos.model.Photo

interface FavoritesRepository {

    interface Listener {
        fun onFavoriteChanged(photo: Photo, isFavorite: Boolean)
        fun onFavoritesChanged()
    }

    fun subscribe(listener: Listener)
    fun unsubscribe(listener: Listener)

    fun getFavoritesLiveData(sortBy: SortBy = SortBy.DATE_ADDED_NEWEST): LiveData<List<FavoritePhotoEntity>>

    /**
     * Inverts isFavorite flag for [photo] and notifies all subscribers that [photo] has been changed.
     */
    fun invertFavorite(photo: Photo)

    suspend fun markAllAsRemoved(): Boolean
    fun unmarkAllAsRemoved()
    fun removeAll()

    /**
     * Used to get [photo]'s isFavorite flag. If [photo] is contained in [favorites] map
     * then this function returns overridden local result from the map, if not, the initial one.
     */
    fun isFavorite(photo: Photo?): Boolean

    suspend fun isFavoriteFromDatabase(photo: Photo): Boolean
    suspend fun populateFavorite(photo: Photo): Photo
}
