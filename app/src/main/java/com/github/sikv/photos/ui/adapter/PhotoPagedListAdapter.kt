package com.github.sikv.photos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.github.sikv.photos.App
import com.github.sikv.photos.R
import com.github.sikv.photos.manager.FavoritesManager
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import javax.inject.Inject

class PhotoPagedListAdapter(
        private val clickCallback: (Photo, View) -> Unit,
        private val longClickCallback: ((Photo, View) -> Unit)? = null,
        private val favoriteClickCallback: ((Photo) -> Unit)? = null
) : PagedListAdapter<Photo, PhotoViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                    oldItem.getPhotoId() == newItem.getPhotoId()

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                    oldItem == newItem
        }
    }

    @Inject
    lateinit var favoritesManager: FavoritesManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun notifyPhotoChanged(photo: Photo) {
        currentList?.indexOf(photo)?.let {
            notifyItemChanged(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        val favorite = favoritesManager.isFavorite(photo)

        holder.bind(photo, favorite, clickCallback, longClickCallback, favoriteClickCallback)
    }
}