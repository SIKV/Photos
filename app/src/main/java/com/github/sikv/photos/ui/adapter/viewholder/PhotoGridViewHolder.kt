package com.github.sikv.photos.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sikv.photos.App
import com.github.sikv.photos.ui.adapter.OnPhotoActionListener
import com.github.sikv.photos.ui.adapter.PhotoGridItem
import com.github.sikv.photos.util.OnHoldReleaseListener
import com.github.sikv.photos.util.PHOTO_TRANSITION_DURATION
import com.github.sikv.photos.util.setOnHoldReleaseListener
import javax.inject.Inject

class PhotoGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @Inject
    lateinit var glide: RequestManager

    init {
        App.instance.appComponent.inject(this)
    }

    fun bind(item: PhotoGridItem, listener: OnPhotoActionListener) {
        item.items.forEach { pair ->
            val imageView = itemView.findViewById<ImageView>(pair.second)

            imageView.visibility = View.VISIBLE

            imageView.setImageDrawable(null)

            pair.first?.let { photo ->
                glide.load(photo.getPhotoPreviewUrl())
                        .transition(DrawableTransitionOptions.withCrossFade(PHOTO_TRANSITION_DURATION))
                        .into(imageView)

                imageView.setOnClickListener { view ->
                    listener.onPhotoAction(OnPhotoActionListener.Action.CLICK, photo, view)
                }

                imageView.setOnHoldReleaseListener(object : OnHoldReleaseListener() {
                    override fun onHold(view: View) {
                        listener.onPhotoAction(OnPhotoActionListener.Action.HOLD, photo, view)
                    }

                    override fun onRelease(view: View) {
                        listener.onPhotoAction(OnPhotoActionListener.Action.RELEASE, photo, view)
                    }
                })
            } ?: run {
                imageView.visibility = View.INVISIBLE
            }
        }
    }
}