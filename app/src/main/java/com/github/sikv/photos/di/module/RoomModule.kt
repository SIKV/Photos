package com.github.sikv.photos.di.module

import android.content.Context
import androidx.room.Room
import com.github.sikv.photos.database.CuratedDb
import com.github.sikv.photos.database.FavoritesDb
import com.github.sikv.photos.database.dao.CuratedDao
import com.github.sikv.photos.database.dao.FavoritesDao
import com.github.sikv.photos.database.dao.RemotePageDao
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
        private const val FAVORITES_DB = "favorites.db"
        private const val CURATED_DB = "curated.db"
    }

    @Singleton
    @Provides
    fun provideFavoritesDatabase(@ApplicationContext context: Context): FavoritesDb {
        return Room.databaseBuilder(context.applicationContext, FavoritesDb::class.java, FAVORITES_DB)
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideCuratedDatabase(@ApplicationContext context: Context): CuratedDb {
        return Room.databaseBuilder(context.applicationContext, CuratedDb::class.java, CURATED_DB)
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideFavoritesDao(favoritesDb: FavoritesDb): FavoritesDao {
        return favoritesDb.favoritesDao
    }

    @Singleton
    @Provides
    fun provideCuratedDao(curatedDb: CuratedDb): CuratedDao {
        return curatedDb.curatedDao
    }

    @Singleton
    @Provides
    fun provideRemotePageDao(curatedDb: CuratedDb): RemotePageDao {
        return curatedDb.remotePageDao
    }
}