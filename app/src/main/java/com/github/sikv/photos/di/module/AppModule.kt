package com.github.sikv.photos.di.module

import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.App
import com.github.sikv.photos.account.AccountManager
import com.github.sikv.photos.account.AccountManagerImpl
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.config.RemoteConfigProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providePreferences(): SharedPreferences {
        return App.instance.getPrivatePreferences()
    }

    @Provides
    fun provideConfigProvider(): ConfigProvider {
        return RemoteConfigProvider()
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