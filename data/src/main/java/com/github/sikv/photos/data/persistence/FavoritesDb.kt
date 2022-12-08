package com.github.sikv.photos.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FavoritePhotoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FavoritesDb : RoomDatabase() {

    abstract val favoritesDao: FavoritesDao
}
