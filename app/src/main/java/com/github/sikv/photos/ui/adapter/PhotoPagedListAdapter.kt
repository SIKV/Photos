package com.github.sikv.photos.ui.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder

class PhotoPagedListAdapter(
        private val glide: RequestManager,
        private val clickCallback: (Photo, View) -> Unit,
        private val longClickCallback: ((Photo, View) -> Unit)? = null

) : PagedListAdapter<Photo, PhotoViewHolder>(PHOTO_COMPARATOR) {

    companion object {
        val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo?, newItem: Photo?): Boolean =
                    oldItem?.getPhotoId() == newItem?.getPhotoId()

            override fun areContentsTheSame(oldItem: Photo?, newItem: Photo?): Boolean =
                    oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)

        return PhotoViewHolder(view, glide)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), clickCallback, longClickCallback)
    }
}