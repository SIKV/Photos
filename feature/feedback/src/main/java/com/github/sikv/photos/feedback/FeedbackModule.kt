package com.github.sikv.photos.feedback

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FeedbackModule {

    @Binds
    abstract fun bindFeedbackRepository(impl: FeedbackRepositoryImpl): FeedbackRepository
}
