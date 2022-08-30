package com.github.sikv.photos.di.module

import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.FeedbackRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.data.repository.impl.FavoritesRepositoryImpl
import com.github.sikv.photos.data.repository.impl.FeedbackRepositoryImpl
import com.github.sikv.photos.data.repository.impl.PhotosRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun providePhotosRepository(photosRepository: PhotosRepositoryImpl): PhotosRepository {
        return photosRepository
    }

    @Provides
    fun provideFavoritesRepository(favoritesRepository: FavoritesRepositoryImpl): FavoritesRepository {
        return favoritesRepository
    }

    @Provides
    fun provideFeedbackRepository(feedbackRepository: FeedbackRepositoryImpl): FeedbackRepository {
        return feedbackRepository
    }
}
