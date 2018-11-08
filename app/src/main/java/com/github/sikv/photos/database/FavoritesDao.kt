package com.github.sikv.photos.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface FavoritesDao {

    @Insert(onConflict = REPLACE)
    fun insertPhoto(photo: PhotoData)

    @Delete
    fun deletePhoto(photo: PhotoData)

    @Query("SELECT * from PhotoData")
    fun getAll(): List<PhotoData>

    @Query("DELETE from PhotoData")
    fun deleteAll()
}