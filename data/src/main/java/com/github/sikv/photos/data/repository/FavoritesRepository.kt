package com.github.sikv.photos.data.repository

import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.data.SortBy
import com.github.sikv.photos.data.persistence.FavoritePhotoEntity
import kotlinx.coroutines.flow.Flow

// TODO: This repository should be refactored (simplified).
interface FavoritesRepository {

    sealed interface Update

    data class UpdatePhoto(
        val photo: Photo,
        val isFavorite: Boolean
    ) : Update

    object UpdateAll : Update

    fun favoriteUpdates(): Flow<Update>

    fun getFavorites(sortBy: SortBy = SortBy.DATE_ADDED_NEWEST): Flow<List<FavoritePhotoEntity>>
    fun getRandom(): FavoritePhotoEntity?

    /**
     * Inverts isFavorite flag for [photo] and notifies all subscribers that [photo] has been changed.
     */
    fun invertFavorite(photo: Photo)

    suspend fun markAllAsDeleted(): Boolean
    suspend fun unmarkAllAsDeleted()
    suspend fun deleteAllMarked()

    /**
     * Used to get [photo]'s isFavorite flag. If [photo] is contained in [favorites] map
     * then this function returns overridden local result from the map, if not, the initial one.
     */
    fun isFavorite(photo: Photo?): Boolean

    suspend fun isFavoriteFromDatabase(photo: Photo): Boolean
    suspend fun populateFavorite(photo: Photo): Photo
}
