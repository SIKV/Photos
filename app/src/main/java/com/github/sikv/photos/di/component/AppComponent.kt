package com.github.sikv.photos.di.component

import android.content.Context
import com.github.sikv.photos.di.module.GlideModule
import com.github.sikv.photos.di.module.RoomModule
import com.github.sikv.photos.ui.adapter.PhotoGridViewHolder
import com.github.sikv.photos.ui.adapter.PhotoListAdapter
import com.github.sikv.photos.ui.adapter.PhotoPagedListAdapter
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.viewmodel.*
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

    fun inject(viewModel: PhotosViewModel)
    fun inject(viewModel: SearchViewModel)
    fun inject(viewModel: SearchDashboardViewModel)
    fun inject(viewModel: PhotoViewModel)
    fun inject(viewModel: SetWallpaperViewModel)
    fun inject(viewModel: FavoritesViewModel)
    fun inject(viewModel: PreferenceViewModel)
    fun inject(viewModel: FeedbackViewModel)
    fun inject(viewHolder: PhotoViewHolder)
    fun inject(viewHolder: PhotoGridViewHolder)
    fun inject(adapter: PhotoPagedListAdapter)
    fun inject(adapter: PhotoListAdapter)
    fun inject(popup: PhotoPreviewPopup)
}