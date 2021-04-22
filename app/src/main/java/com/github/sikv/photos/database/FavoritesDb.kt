package com.github.sikv.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.sikv.photos.database.dao.FavoritesDao
import com.github.sikv.photos.database.entity.FavoritePhotoEntity

@Database(
        entities = [FavoritePhotoEntity::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FavoritesDb : RoomDatabase() {

    abstract val favoritesDao: FavoritesDao
}