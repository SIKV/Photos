package com.github.sikv.photos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(PhotoData::class)], version = 1, exportSchema = false)
abstract class FavoritesDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "favorites.db"

        @Volatile
        private var INSTANCE: FavoritesDatabase? = null

        fun getInstance(context: Context): FavoritesDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, FavoritesDatabase::class.java, DATABASE_NAME)
                            .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

    abstract val favoritesDao: FavoritesDao
}