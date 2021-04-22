package com.github.sikv.photos.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.github.sikv.photos.config.DbConfig
import com.github.sikv.photos.database.entity.CuratedPhotoEntity

@Dao
interface CuratedDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(photos: List<CuratedPhotoEntity>)

    @Query("SELECT * FROM ${DbConfig.curatedPhotosTableName}")
    fun pagingSource(): PagingSource<Int, CuratedPhotoEntity>

    @Query("DELETE FROM ${DbConfig.curatedPhotosTableName}")
    fun clearAll()
}