package com.github.sikv.photos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.R
import com.github.sikv.photos.database.PhotoData
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder

class PhotoDataListAdapter(
        private val glide: RequestManager,
        private val clickCallback: (PhotoData, View) -> Unit,
        private val longClickCallback: ((PhotoData, View) -> Unit)? = null

) : ListAdapter<PhotoData, PhotoViewHolder>(PHOTO_DATA_COMPARATOR) {

    companion object {
        val PHOTO_DATA_COMPARATOR = object : DiffUtil.ItemCallback<PhotoData>() {
            override fun areItemsTheSame(oldItem: PhotoData, newItem: PhotoData): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PhotoData, newItem: PhotoData): Boolean =
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