package com.github.sikv.photos.di

import android.content.Context
import com.github.sikv.photos.common.GlidePhotoLoader
import com.github.sikv.photos.config.FeatureFlagRepository
import com.github.sikv.photos.config.RemoteFeatureFlagRepository
import com.github.sikv.photos.common.PhotoLoader
import com.github.sikv.photos.common.AccountManager
import com.github.sikv.photos.common.AccountManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideFeatureFlagRepository(): FeatureFlagRepository {
        return RemoteFeatureFlagRepository()
    }

    @Provides
    fun provideAccountManager(accountManager: AccountManagerImpl): AccountManager {
        return accountManager
    }

    @Provides
    fun providePhotoLoader(@ApplicationContext context: Context): PhotoLoader {
        return GlidePhotoLoader(context)
    }
}
