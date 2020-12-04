package com.github.sikv.photos.di.component

import android.content.Context
import com.github.sikv.photos.data.PexelsCuratedPhotosPagingSource
import com.github.sikv.photos.data.SearchPhotosPagingSource
import com.github.sikv.photos.di.module.*
import com.github.sikv.photos.ui.adapter.PhotoListAdapter
import com.github.sikv.photos.ui.adapter.PhotoPagingAdapter
import com.github.sikv.photos.ui.adapter.viewholder.PhotoGridViewHolder
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import com.github.sikv.photos.ui.fragment.MoreFragment
import com.github.sikv.photos.ui.fragment.SearchDashboardFragment
import com.github.sikv.photos.ui.fragment.SingleSearchFragment
import com.github.sikv.photos.ui.popup.PhotoPreviewPopup
import com.github.sikv.photos.viewmodel.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    RoomModule::class,
    RepositoryModule::class,
    ViewModelModule::class,
    GlideModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: SearchDashboardFragment)
    fun inject(fragment: SingleSearchFragment)
    fun inject(fragment: MoreFragment.PreferenceFragment)

    fun inject(viewModel: PhotosViewModel)
    fun inject(viewModel: SearchViewModel)
    fun inject(viewModel: SearchDashboardViewModel)
    fun inject(viewModel: PhotoViewModel)
    fun inject(viewModel: SetWallpaperViewModel)
    fun inject(viewModel: FavoritesViewModel)
    fun inject(viewModel: MoreViewModel)
    fun inject(viewModel: FeedbackViewModel)

    fun inject(viewHolder: PhotoViewHolder)
    fun inject(viewHolder: PhotoGridViewHolder)

    fun inject(popup: PhotoPreviewPopup)

    fun inject(adapter: PhotoPagingAdapter)
    fun inject(adapter: PhotoListAdapter)

    fun inject(source: SearchPhotosPagingSource)
    fun inject(source: PexelsCuratedPhotosPagingSource)
}