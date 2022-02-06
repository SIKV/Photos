package com.github.sikv.photos.di.module

import android.content.Context
import com.github.sikv.photos.config.feature.FeatureFlagRepository
import com.github.sikv.photos.config.feature.RemoteFeatureFlagRepository
import com.github.sikv.photos.manager.AccountManager
import com.github.sikv.photos.manager.AccountManagerImpl
import com.github.sikv.photos.manager.GlidePhotoLoader
import com.github.sikv.photos.manager.PhotoLoader
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
