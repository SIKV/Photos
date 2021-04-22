package com.github.sikv.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.sikv.photos.database.dao.CuratedDao
import com.github.sikv.photos.database.dao.RemotePageDao
import com.github.sikv.photos.database.entity.CuratedPhotoEntity
import com.github.sikv.photos.database.entity.RemotePageEntity

@Database(
        entities = [CuratedPhotoEntity::class, RemotePageEntity::class],
        version = 1,
        exportSchema = false
)
abstract class CuratedDb : RoomDatabase() {

    abstract val curatedDao: CuratedDao
    abstract val remotePageDao: RemotePageDao
}