package com.github.sikv.photos.di.component

import android.content.Context
import com.github.sikv.photos.di.module.GlideModule
import com.github.sikv.photos.di.module.RoomModule
import com.github.sikv.photos.service.DownloadPhotoService
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import com.github.sikv.photos.viewmodel.FavoritesViewModel
import com.github.sikv.photos.viewmodel.MainViewModel
import com.github.sikv.photos.viewmodel.PhotoViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RoomModule::class, GlideModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(service: DownloadPhotoService)
    fun inject(viewModel: MainViewModel)
    fun inject(viewModel: PhotoViewModel)
    fun inject(viewModel: FavoritesViewModel)
    fun inject(viewHolder: PhotoViewHolder)
}