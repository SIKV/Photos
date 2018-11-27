package com.github.sikv.photos.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface FavoritesDao {

    @Insert(onConflict = REPLACE)
    fun insert(photo: PhotoData)

    @Delete
    fun delete(photo: PhotoData)

    @Query("SELECT * from PhotoData")
    fun getAll(): LiveData<List<PhotoData>>

    @Query("DELETE from PhotoData")
    fun deleteAll()
}