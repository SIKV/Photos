package com.github.sikv.photos.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder
import javax.inject.Inject

class PhotoListAdapter(
        private val listener: OnPhotoActionListener
) : ListAdapter<Photo, PhotoViewHolder>(Photo.COMPARATOR) {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    private var itemLayoutType = PhotoItemLayoutType.FULL

    init {
        App.instance.appComponent.inject(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                listener.onPhotoActionParentRelease()
            }

            return@setOnTouchListener false
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

        holder.bind(itemLayoutType, photo, favorite, listener)
    }
}