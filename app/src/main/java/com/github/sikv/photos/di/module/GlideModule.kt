package com.github.sikv.photos.di.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides

@Module
class GlideModule(val context: Context) {

    @Provides
    fun provideGlide(): RequestManager {
        return Glide.with(context)
    }
}