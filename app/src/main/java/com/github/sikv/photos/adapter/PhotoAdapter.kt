package com.github.sikv.photos.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sikv.photos.R
import com.github.sikv.photos.adapter.viewholder.PhotoViewHolder
import com.github.sikv.photos.model.Photo

class PhotoAdapter(private val clickListener: (Photo, View) -> Unit) :
        RecyclerView.Adapter<PhotoViewHolder>() {

    private var photos: List<Photo> = listOf()

    fun setItems(photos: List<Photo>) {
        this.photos = photos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)

        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position], clickListener)
    }
}