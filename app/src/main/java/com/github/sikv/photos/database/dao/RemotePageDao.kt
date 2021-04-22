package com.github.sikv.photos.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.github.sikv.photos.config.DbConfig
import com.github.sikv.photos.database.entity.RemotePageEntity

@Dao
interface RemotePageDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertOrReplace(remotePage: RemotePageEntity)

    @Query("SELECT * FROM ${DbConfig.remotePagesTableName} WHERE label=:query")
    suspend fun remotePageByQuery(query: String): RemotePageEntity?

    @Query("DELETE FROM ${DbConfig.remotePagesTableName} WHERE label=:query")
    suspend fun deleteByQuery(query: String)
}