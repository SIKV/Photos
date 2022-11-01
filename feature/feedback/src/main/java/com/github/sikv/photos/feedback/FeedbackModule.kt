package com.github.sikv.photos.feedback

import com.github.sikv.photos.feedback.data.FeedbackRepository
import com.github.sikv.photos.feedback.data.FeedbackRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FeedbackModule {

    @Binds
    abstract fun bindFeedbackRepository(impl: FeedbackRepositoryImpl): FeedbackRepository
}
