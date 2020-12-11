package com.github.sikv.photos.di.module

import android.content.Context
import androidx.room.Room
import com.github.sikv.photos.database.FavoritesDao
import com.github.sikv.photos.database.FavoritesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RoomModule {

    companion object {
        private const val DATABASE_NAME = "favorites.db"
    }

    @Singleton
    @Provides
    fun provideFavoritesDatabase(@ApplicationContext context: Context): FavoritesDatabase {
        return Room.databaseBuilder(context.applicationContext, FavoritesDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideFavoritesDao(favoritesDatabase: FavoritesDatabase): FavoritesDao {
        return favoritesDatabase.favoritesDao
    }
}