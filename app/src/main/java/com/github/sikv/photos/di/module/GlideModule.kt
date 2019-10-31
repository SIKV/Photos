package com.github.sikv.photos.di.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides

@Module
class GlideModule {

    @Provides
    fun provideGlide(context: Context): RequestManager {
        return Glide.with(context)
    }
}