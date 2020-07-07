package com.github.sikv.photos.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface FavoritesDao {

    @Insert(onConflict = REPLACE)
    fun insert(photo: FavoritePhotoEntity)

    @RawQuery(observedEntities = [FavoritePhotoEntity::class])
    fun getPhotos(query: SupportSQLiteQuery): LiveData<List<FavoritePhotoEntity>>

    @Query("SELECT * FROM FavoritePhoto WHERE id=:id AND markedAsDeleted=0")
    fun getById(id: String): FavoritePhotoEntity?

    @Query("SELECT * FROM FavoritePhoto WHERE markedAsDeleted=0 ORDER BY RANDOM() LIMIT 1")
    fun getRandom(): FavoritePhotoEntity?

    @Query("SELECT COUNT(*) FROM FavoritePhoto WHERE markedAsDeleted=0")
    fun getCount(): Int

    @Query("UPDATE FavoritePhoto SET markedAsDeleted=1")
    fun markAllAsDeleted()

    @Query("UPDATE FavoritePhoto SET markedAsDeleted=0")
    fun unmarkAllAsDeleted()

    @Delete
    fun delete(photo: FavoritePhotoEntity)

    @Query("DELETE from FavoritePhoto")
    fun deleteAll()
}