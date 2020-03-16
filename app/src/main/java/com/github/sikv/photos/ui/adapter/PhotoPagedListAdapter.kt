package com.github.sikv.photos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.PhotoItemClickSource
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import javax.inject.Inject

class PhotoPagedListAdapter(
        private val clickCallback: (PhotoItemClickSource, Photo, View) -> Unit
) : PagedListAdapter<Photo, PhotoViewHolder>(Photo.COMPARATOR) {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    private var itemLayoutType = PhotoItemLayoutType.FULL

    init {
        App.instance.appComponent.inject(this)
    }

    fun notifyPhotoChanged(photo: Photo) {
        currentList?.indexOf(photo)?.let {
            notifyItemChanged(it)
        }
    }

    fun setItemLayoutType(itemLayoutType: PhotoItemLayoutType) {
        this.itemLayoutType = itemLayoutType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutType.layout, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        val favorite = favoritesRepository.isFavorite(photo)

        holder.bind(itemLayoutType, photo, favorite, clickCallback)
    }
}