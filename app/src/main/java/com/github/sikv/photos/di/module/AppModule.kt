package com.github.sikv.photos.di.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.manager.AccountManager
import com.github.sikv.photos.manager.AccountManagerImpl
import com.github.sikv.photos.config.feature.FeatureFlagRepository
import com.github.sikv.photos.config.feature.RemoteFeatureFlagRepository
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
    fun provideGlide(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context)
    }

    @Provides
    fun provideAccountManager(accountManager: AccountManagerImpl): AccountManager {
        return accountManager
    }
}