package com.github.sikv.photos.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.PhotoItemLayoutType
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.PhotoDiffUtil
import com.github.sikv.photos.ui.adapter.viewholder.PhotoViewHolder

class PhotoPagingAdapter<T : Photo>(
    private val glide: RequestManager,
    private val favoritesRepository: FavoritesRepository,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val listener: OnPhotoActionListener
) : PagingDataAdapter<T, PhotoViewHolder>(PhotoDiffUtil()) {

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

    fun notifyPhotoChanged(photo: Photo?) {
        notifyItemChanged(snapshot().indexOf(photo))
    }

    fun setItemLayoutType(itemLayoutType: PhotoItemLayoutType) {
        this.itemLayoutType = itemLayoutType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutType.layout, parent, false)
        return PhotoViewHolder(view, glide, lifecycleScope)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        val isFavorite = favoritesRepository.isFavorite(photo)

        holder.bind(itemLayoutType, photo, isFavorite, listener)
    }
}
