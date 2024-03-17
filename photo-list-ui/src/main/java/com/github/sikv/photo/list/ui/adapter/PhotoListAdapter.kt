package com.github.sikv.photo.list.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.sikv.photo.list.ui.OnPhotoActionListener
import com.github.sikv.photo.list.ui.PhotoDiffUtil
import com.github.sikv.photo.list.ui.PhotoItemLayoutType
import com.github.sikv.photos.common.PhotoLoader
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.data.repository.FavoritesRepository

// TODO: FavoritesRepository SHOULD NOT be used here.
class PhotoListAdapter(
    private val photoLoader: PhotoLoader,
    private val favoritesRepository: FavoritesRepository,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val listener: OnPhotoActionListener
) : ListAdapter<Photo, PhotoViewHolder>(PhotoDiffUtil()) {

    private var itemLayoutType = PhotoItemLayoutType.FULL

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
        return PhotoViewHolder(view, photoLoader, lifecycleScope)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        val isFavorite = favoritesRepository.isFavorite(photo)

        holder.bind(itemLayoutType, photo, isFavorite, listener)
    }
}
