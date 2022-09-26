package com.github.sikv.photos.persistence

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = REPLACE)
    fun insert(photo: FavoritePhotoEntity)

    @RawQuery(observedEntities = [FavoritePhotoEntity::class])
    fun getPhotos(query: SupportSQLiteQuery): Flow<List<FavoritePhotoEntity>>

    @Query("SELECT * FROM ${DbConfig.favoritePhotosTableName} WHERE id=:id AND markedAsDeleted=0")
    fun getById(id: String): FavoritePhotoEntity?

    @Query("SELECT * FROM ${DbConfig.favoritePhotosTableName} WHERE markedAsDeleted=0 ORDER BY RANDOM() LIMIT 1")
    fun getRandom(): FavoritePhotoEntity?

    @Query("SELECT COUNT(*) FROM ${DbConfig.favoritePhotosTableName} WHERE markedAsDeleted=0")
    fun getCount(): Int

    @Query("UPDATE ${DbConfig.favoritePhotosTableName} SET markedAsDeleted=1")
    fun markAllAsDeleted()

    @Query("UPDATE ${DbConfig.favoritePhotosTableName} SET markedAsDeleted=0")
    fun unmarkAllAsDeleted()

    @Delete
    fun delete(photo: FavoritePhotoEntity)

    @Query("DELETE from ${DbConfig.favoritePhotosTableName}")
    fun deleteAll()
}
