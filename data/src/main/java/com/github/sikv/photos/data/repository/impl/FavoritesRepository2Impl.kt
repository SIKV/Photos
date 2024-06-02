package com.github.sikv.photos.data.repository.impl

import com.github.sikv.photos.data.SortBy
import com.github.sikv.photos.data.persistence.FavoritePhotoEntity
import com.github.sikv.photos.data.persistence.FavoritesDao
import com.github.sikv.photos.data.persistence.FavoritesDbQueryBuilder
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.FavoritesRepository2
import com.github.sikv.photos.domain.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository2Impl @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val queryBuilder: FavoritesDbQueryBuilder
) : FavoritesRepository2 {

    override suspend fun isFavorite(photo: Photo): Boolean =
        withContext(Dispatchers.IO) {
            favoritesDao.getById(photo.getPhotoId()) != null
        }

    override suspend fun invertFavorite(photo: Photo) {
        withContext(Dispatchers.IO) {
            val favorite = !isFavorite(photo)
            val favoritePhoto = FavoritePhotoEntity.fromPhoto(photo)

            if (favorite) {
                favoritesDao.insert(favoritePhoto)
            } else {
                favoritesDao.delete(favoritePhoto)
            }
        }
    }

    override fun getFavorites(sortBy: SortBy): Flow<List<FavoritePhotoEntity>> {
        val query = queryBuilder.buildGetPhotosQuery(sortBy)
        return favoritesDao.getPhotos(query).map { photos ->
            photos.onEach { it.favorite = true }
        }
    }

    override fun getRandom(): FavoritePhotoEntity? = favoritesDao.getRandom()

    override suspend fun markAllAsDeleted(): Boolean = withContext(Dispatchers.IO) {
        val count = favoritesDao.getCount()

        if (count > 0) {
            favoritesDao.markAllAsDeleted()
            true
        } else {
            false
        }
    }

    override suspend fun unmarkAllAsDeleted() = withContext(Dispatchers.IO) {
        favoritesDao.unmarkAllAsDeleted()
    }

    override suspend fun deleteAllMarked() = withContext(Dispatchers.IO) {
        favoritesDao.deleteAllMarked()
    }
}
