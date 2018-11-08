package com.github.sikv.photos.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(PhotoData::class)], version = 1)
abstract class FavoritesDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "favorites.db"

        private var instance: FavoritesDatabase? = null

        fun getInstance(context: Context): FavoritesDatabase? {
            if (instance == null) {
                synchronized(FavoritesDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext, FavoritesDatabase::class.java, DB_NAME)
                            .build()
                }
            }

            return instance
        }

        fun destroyInstance() {
            instance = null
        }
    }

    abstract fun photoDao(): FavoritesDao
}