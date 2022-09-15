package com.github.sikv.photos.data.repository

import com.github.sikv.photos.data.repository.impl.FavoritesRepositoryImpl
import com.github.sikv.photos.data.repository.impl.PhotosRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPhotosRepository(photosRepository: PhotosRepositoryImpl): PhotosRepository

    @Binds
    abstract fun bindFavoritesRepository(favoritesRepository: FavoritesRepositoryImpl): FavoritesRepository
}
