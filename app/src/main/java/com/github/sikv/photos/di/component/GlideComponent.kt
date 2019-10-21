package com.github.sikv.photos.di.component

import com.github.sikv.photos.di.module.GlideModule
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import com.github.sikv.photos.viewmodel.PhotoViewModel
import dagger.Component

@Component(modules = [GlideModule::class])
interface GlideComponent {

    fun inject(photoViewModel: PhotoViewModel)
    fun inject(photoViewHolder: PhotoViewHolder)
}