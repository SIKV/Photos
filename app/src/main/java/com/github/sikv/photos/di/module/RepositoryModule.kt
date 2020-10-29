package com.github.sikv.photos.di.module

import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.data.repository.impl.FavoritesRepositoryImpl
import com.github.sikv.photos.data.repository.impl.PhotosRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun providePhotosRepository(photosRepository: PhotosRepositoryImpl): PhotosRepository {
        return photosRepository
    }

    @Provides
    fun provideFavoritesRepository(favoritesRepository: FavoritesRepositoryImpl): FavoritesRepository {
        return favoritesRepository
    }
}