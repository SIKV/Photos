package com.github.sikv.photos.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(FavoritePhotoEntity::class)], version = 1, exportSchema = false)
abstract class FavoritesDatabase : RoomDatabase() {

    abstract val favoritesDao: FavoritesDao
}