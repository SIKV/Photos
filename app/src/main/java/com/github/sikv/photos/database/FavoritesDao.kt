package com.github.sikv.photos.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface FavoritesDao {

    @Insert(onConflict = REPLACE)
    fun insert(photo: FavoritePhotoEntity)

    @Delete
    fun delete(photo: FavoritePhotoEntity)

    @Query("SELECT * from FavoritePhoto")
    fun getAll(): LiveData<List<FavoritePhotoEntity>>

    @Query("SELECT * from FavoritePhoto")
    fun getAllList(): List<FavoritePhotoEntity>

    @Query("SELECT * FROM FavoritePhoto WHERE id=:id")
    fun getById(id: String): FavoritePhotoEntity?

    @Query("SELECT * FROM FavoritePhoto ORDER BY RANDOM() LIMIT 1")
    fun getRandom(): FavoritePhotoEntity?

    @Query("SELECT COUNT(*) FROM FavoritePhoto")
    fun getCount(): Int

    @Query("DELETE from FavoritePhoto")
    fun deleteAll()
}