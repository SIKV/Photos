package com.github.sikv.photos.config

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ConfigModule {

    @Binds
    abstract fun bindFeatureFlagRepository(repository: RemoteFeatureFlagRepository): FeatureFlagRepository
}
