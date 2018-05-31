package com.github.sikv.photos.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    fun bind(photo: Photo, clickListener: (Photo, View) -> Unit) {
        Glide.with(itemView.context)
                .load(photo.urls.small)
                .into(itemView.itemPhotoImage)

        itemView.setOnClickListener {
            clickListener.invoke(photo, it)
        }
    }
}