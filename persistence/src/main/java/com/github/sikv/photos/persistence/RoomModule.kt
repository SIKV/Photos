package com.github.sikv.photos.persistence

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    companion object {
        private const val FAVORITES_DB = "favorites.db"
    }

    @Provides
    @Singleton
    fun provideFavoritesDatabase(@ApplicationContext context: Context): FavoritesDb {
        return Room
            .databaseBuilder(
                context.applicationContext,
                FavoritesDb::class.java,
                FAVORITES_DB
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFavoritesDao(favoritesDb: FavoritesDb): FavoritesDao {
        return favoritesDb.favoritesDao
    }
}
