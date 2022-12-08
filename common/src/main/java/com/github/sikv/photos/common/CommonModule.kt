package com.github.sikv.photos.common

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun providePhotoLoader(@ApplicationContext context: Context): PhotoLoader {
        return GlidePhotoLoader(context)
    }
}
