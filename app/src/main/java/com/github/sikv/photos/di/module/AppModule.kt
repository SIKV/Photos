package com.github.sikv.photos.di.module

import com.github.sikv.photos.account.AccountManager
import com.github.sikv.photos.account.AccountManagerImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideAccountManager(accountManager: AccountManagerImpl): AccountManager {
        return accountManager
    }
}