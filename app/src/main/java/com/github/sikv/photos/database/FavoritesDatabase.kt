package com.github.sikv.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [FavoritePhotoEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract val favoritesDao: FavoritesDao
}