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
    fun insert(photo: PhotoData)

    @Delete
    fun delete(photo: PhotoData)

    @Query("SELECT * from PhotoData")
    fun getAll(): LiveData<List<PhotoData>>

    @Query("SELECT * from PhotoData")
    fun getAllList(): List<PhotoData>

    @Query("SELECT * FROM PhotoData WHERE id=:id")
    fun getById(id: String): PhotoData?

    @Query("SELECT COUNT(*) FROM PhotoData")
    fun getCount(): Int

    @Query("DELETE from PhotoData")
    fun deleteAll()
}