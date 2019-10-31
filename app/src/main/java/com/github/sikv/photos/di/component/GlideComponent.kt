package com.github.sikv.photos.di.component

import android.content.Context
import com.github.sikv.photos.di.module.GlideModule
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import com.github.sikv.photos.viewmodel.PhotoViewModel
import dagger.BindsInstance
import dagger.Component

@Component(modules = [GlideModule::class])
interface GlideComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): GlideComponent
    }

    fun inject(photoViewModel: PhotoViewModel)
    fun inject(photoViewHolder: PhotoViewHolder)
}