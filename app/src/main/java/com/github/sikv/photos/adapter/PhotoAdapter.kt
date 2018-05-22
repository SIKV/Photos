package com.github.sikv.photos.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.adapter.viewholder.PhotoViewHolder

class PhotoAdapter : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)

        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 50
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind()
    }
}