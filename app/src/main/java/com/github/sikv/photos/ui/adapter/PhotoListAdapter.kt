package com.github.sikv.photos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import javax.inject.Inject

class PhotoListAdapter(
        private val clickCallback: (Photo, View) -> Unit,
        private val longClickCallback: ((Photo, View) -> Unit)? = null,
        private val favoriteClickCallback: ((Photo) -> Unit)? = null
) : ListAdapter<Photo, PhotoViewHolder>(Photo.COMPARATOR) {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    private var itemLayoutType = PhotoItemLayoutType.FULL

    init {
        App.instance.appComponent.inject(this)
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

        holder.bind(itemLayoutType, photo, favorite, clickCallback, longClickCallback, favoriteClickCallback)
    }
}