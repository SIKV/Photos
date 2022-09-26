package com.github.sikv.photo.list.ui

import androidx.recyclerview.widget.DiffUtil
import com.github.sikv.photos.domain.Photo

class PhotoDiffUtil<T : Photo> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.getPhotoId() == newItem.getPhotoId()

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem
}
