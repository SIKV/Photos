package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sikv.photos.App
import com.github.sikv.photos.database.PhotoData
import com.github.sikv.photos.model.Photo
import kotlinx.android.synthetic.main.item_photo.view.*
import javax.inject.Inject

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        private const val TRANSITION_DURATION = 1000
    }

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun bind(photo: Photo?,
             clickCallback: (Photo, View) -> Unit,
             longClickCallback: ((Photo, View) -> Unit)? = null) {

        itemView.itemPhotoImage.setImageDrawable(null)
        itemView.setOnClickListener(null)

        photo?.let {
            glide.load(photo.getSmallUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(TRANSITION_DURATION))
                    .into(itemView.itemPhotoImage)

            itemView.setOnClickListener {
                clickCallback.invoke(photo, it)
            }

            itemView.setOnLongClickListener {
                longClickCallback?.invoke(photo, it)
                return@setOnLongClickListener true
            }
        }
    }

    fun bind(photo: PhotoData?,
             clickCallback: (PhotoData, View) -> Unit,
             longClickCallback: ((PhotoData, View) -> Unit)? = null) {

        itemView.itemPhotoImage.setImageDrawable(null)
        itemView.setOnClickListener(null)

        photo?.let {
            glide.load(photo.url)
                    .transition(DrawableTransitionOptions.withCrossFade(TRANSITION_DURATION))
                    .into(itemView.itemPhotoImage)

            itemView.setOnClickListener {
                clickCallback.invoke(photo, it)
            }

            itemView.setOnLongClickListener {
                longClickCallback?.invoke(photo, it)
                return@setOnLongClickListener true
            }
        }
    }
}